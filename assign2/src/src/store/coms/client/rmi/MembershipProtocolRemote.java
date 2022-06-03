package store.coms.client.rmi;

import rmi.MembershipCommands;
import store.node.State;
import store.node.NodeState;
import store.service.JoinMessageSender;
import store.service.JoinServiceThread;
import store.service.LeaveMessageSender;
import store.service.MembershipServiceThread;

import java.io.IOException;
import java.rmi.RemoteException;

public class MembershipProtocolRemote implements MembershipCommands {
    // TODO: maybe put in another package
    private final NodeState nodeState;

    private int retries = 0;

    private int MAX_RETRIES = 2;

    private int joinResponsesCounter = 0;

    private final MembershipServiceThread membershipThread;

    public MembershipProtocolRemote(NodeState nodeState){
        this.nodeState = nodeState;
        this.membershipThread = new MembershipServiceThread(this.nodeState);
    }

    public NodeState getNodeState(){
        return this.nodeState;
    }

    /* If node waiting for client, means that the node has been created but waiting for the join command */
    public synchronized Boolean nodeAlreadyJoining(){
        return !this.nodeState.getState().equals(State.WAITING_FOR_CLIENT);
    }

    public void joinProtocol(){
        nodeState.changeNodeState(State.JOINING);

        if(nodeState.getNodeId().equals("127.0.0.1")){
            System.out.println("Entering join membership protocol");

            // TODO: Method way to confusing needs refactor, asap but only when all things are leveled out

            /*Open TCP port on the joining node, to accept membership messages from the other nodes*/
            JoinServiceThread joinServiceThread = new JoinServiceThread(nodeState);

            /*Upon check if 3 replies. If not retry 2 more times. If it still fails, only listen to membership*/

            joinServiceThread.start();
            /*
                1 -> 2 no A e no B
                2 -> 1 no C
             */
            /* Banana Code needs small refactor, so that timeout can run on a thread, just refactor to future abd execute */

            /* Wait for the private port to be opened in the JoinServiceThread*/
            while(!joinServiceThread.getPortStatus()){}

            JoinMessageSender joinMulticastMessage = new JoinMessageSender(nodeState);

            joinMulticastMessage.run();

            /* Wait for TCP socket to close (TIMEOUT/SUCCESS)*/
            while(joinServiceThread.isAlive());

            

            joinResponsesCounter += joinServiceThread.getConnectionsEstablished();

            if(joinResponsesCounter < 3 && retries < MAX_RETRIES){
                retries++;
                System.out.println("Join protocol need to be issued again! Attempting retry number " + retries + "/" + MAX_RETRIES);
                joinProtocol();
            }

        }
    }

    @Override
    public String join() throws RemoteException {

        if(nodeAlreadyJoining()){
            return "Node with id '" + this.nodeState.getNodeId() + "' has already joined the cluster";
        }

        joinProtocol();

        /* After join protocol was successfully executed */
        try {
            nodeState.getMembershipLogger().updateCounter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nodeState.changeNodeState(State.JOINED);

        /* Start actively listening from multicast after joining */
        membershipThread.start();

        return "Node Id: '" + nodeState.getNodeId() + "' Membership Protocol for multicast join RMI";
    }

    @Override
    public String leave() throws RemoteException {
        //Multicast leave

        LeaveMessageSender leaveMulticastMessage = new LeaveMessageSender(nodeState);


        System.out.println("Sending multicast node to leave");
        leaveMulticastMessage.run();

        //sync status -> leave

        System.out.println("Node status WAITING_FOR_CLIENT");
        nodeState.changeNodeState(State.WAITING_FOR_CLIENT);

        try {
            nodeState.getMembershipLogger().updateCounter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return "Membership Protocol for multicast leave RMI";

    }
}

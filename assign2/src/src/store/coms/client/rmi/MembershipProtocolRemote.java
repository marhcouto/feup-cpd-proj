package store.coms.client.rmi;

import requests.multicast.JoinMembershipMessage;
import rmi.MembershipCommands;
import store.node.State;
import store.node.NodeState;
import store.service.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.rmi.RemoteException;

public class MembershipProtocolRemote implements MembershipCommands {
    // TODO: maybe put in another package
    private final NodeState nodeState;

    private int retries = 0;

    private int MAX_RETRIES = 2;

    private int joinResponsesCounter = 0;
    private ServiceProvider serviceProvider;
    public MembershipProtocolRemote(NodeState nodeState, ServiceProvider serviceProvider){
        this.nodeState = nodeState;
        this.serviceProvider = serviceProvider;
    }

    public NodeState getNodeState(){
        return this.nodeState;
    }

    /*If node waiting for client, means that the node has been created but waiting for the join command*/
    public Boolean nodeAlreadyJoining() {
        return this.nodeState.getState().equals(State.JOINING);
    }

    public void joinProtocol() {
        nodeState.setState(State.JOINING);

        System.out.println("Entering join membership protocol");

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
        while(!joinServiceThread.getPortStatus());

        int privatePort = joinServiceThread.getPort();
        try{
            System.out.println("Sending multicast join message from node: '" + nodeState.getNodeId() + "'");
            MulticastSocket socket;
            socket = new MulticastSocket();
            JoinMembershipMessage joinMessage = new JoinMembershipMessage(Integer.toString(privatePort), nodeState.getMembershipLogger().getMembershipCounter(), nodeState.getNodeId());
            DatagramPacket packet = new DatagramPacket(joinMessage.toString().getBytes(), joinMessage.toString().length(), nodeState.getmCastIpAddress(), nodeState.getmCastPort());
            socket.send(packet);
            socket.close();
            /* Wait for TCP socket to close (TIMEOUT/SUCCESS)*/
            // while(joinServiceThread.isAlive());
            joinServiceThread.join();
        } catch (IOException e){
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        joinResponsesCounter += joinServiceThread.getConnectionsEstablished();

        if(joinResponsesCounter < 3 && retries < MAX_RETRIES){
            retries++;
            System.out.println("Join protocol need to be issued again! Attempting retry number " + retries + "/" + MAX_RETRIES);
            joinProtocol();
        }
    }

    @Override
    public String join() throws RemoteException {

        if(nodeAlreadyJoining()){
            return "Node with id '" + this.nodeState.getNodeId() + "' has already joined the cluster";
        }

        joinProtocol();

        /* After join protocol was successfully executed */
        nodeState.setState(State.JOINED);
        try {
            nodeState.getMembershipLogger().updateCounter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /* Start actively listening from multicast after joining */
        serviceProvider.setupMembershipService();
        serviceProvider.setupDataService();

        return "Node Id: '" + nodeState.getNodeId() + "' Membership Protocol for multicast join RMI";
    }

    @Override
    public String leave() throws RemoteException {
        //Multicast leave
        LeaveMessageSender leaveMulticastMessage = new LeaveMessageSender(nodeState);


        System.out.println("Sending multicast node to leave");
        nodeState.setState(State.LEAVING);
        leaveMulticastMessage.run();

        //sync status -> leave

        System.out.println("Node status WAITING_FOR_CLIENT");
        serviceProvider.stopDataService();
        serviceProvider.stopMembershipService();
        nodeState.setState(State.WAITING_FOR_CLIENT);

        try {
            nodeState.getMembershipLogger().updateCounter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Membership Protocol for multicast leave RMI";

    }
}

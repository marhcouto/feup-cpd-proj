package store.rmi;

import requests.multicast.JoinMembershipMessage;
import requests.multicast.LeaveMembershipMessage;
import rmi.MembershipCommands;
import store.multicast.MulticastMessageSender;
import store.node.State;
import store.node.NodeState;
import store.service.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;

public class MembershipProtocolRemote implements MembershipCommands {
    // TODO: maybe put in another package
    private final NodeState nodeState;
    private int retries = 0;
    private static final int MAX_RETRIES = 2;
    private int joinResponsesCounter = 0;
    private final ServiceProvider serviceProvider;

    public MembershipProtocolRemote(NodeState nodeState, ServiceProvider serviceProvider){
        this.nodeState = nodeState;
        this.serviceProvider = serviceProvider;
    }

    public NodeState getNodeState(){
        return this.nodeState;
    }

    public void joinProtocol() {

        /*Open TCP port on the joining node, to accept membership messages from the other nodes*/
        JoinServiceThread joinServiceThread = new JoinServiceThread(nodeState);

        /*Upon check if 3 replies. If not retry 2 more times. If it still fails, only listen to membership*/

        joinServiceThread.start();


        /* Wait for the private port to be opened in the JoinServiceThread*/
        while(!joinServiceThread.getPortStatus());

        int privatePort = joinServiceThread.getPort();
        try{
            MulticastMessageSender.multicastJoin(nodeState.getNodeId(), nodeState.getMembershipLogger().getMembershipCounter(), nodeState.getmCastIpAddress(), nodeState.getmCastPort(), privatePort);
            joinServiceThread.join();
        } catch (IOException | InterruptedException e){
            throw new RuntimeException(e);
        }

        joinResponsesCounter += joinServiceThread.getConnectionsEstablished();

        if(joinResponsesCounter < 3 && retries < MAX_RETRIES){
            retries++;
            System.out.println("Join protocol needs to be issued again! Attempting retry number " + retries + "/" + MAX_RETRIES);
            joinProtocol();
        }
    }

    @Override
    public String join() throws RemoteException {

        State state = nodeState.getState();

        if(state.equals(State.JOINING)){
            return "Node '" + this.nodeState.getNodeId() + "' is already joining the cluster";
        } else if (state.equals(State.JOINED)) {
            return "Node '" + this.nodeState.getNodeId() + "' has already joined the cluster";
        } else if (state.equals(State.LEAVING)) {
            return "Node '" + this.nodeState.getNodeId() + "' is in process of leaving the cluster";
        }
        try {
            nodeState.getMembershipLogger().updateCounter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nodeState.setState(State.JOINING);
        joinProtocol();
        nodeState.setState(State.JOINED);

        serviceProvider.setupMembershipService();
        System.out.println("JOINED CLUSTER");

        return "Node '" + nodeState.getNodeId() + "' joined cluster successfully";
    }

    @Override
    public String leave() throws RemoteException {

        State state = nodeState.getState();

        if(state.equals(State.JOINING)){
            return "Node '" + this.nodeState.getNodeId() + "' is already joining the cluster";
        } else if (state.equals(State.WAITING_FOR_CLIENT)) {
            return "Node '" + this.nodeState.getNodeId() + "' is waiting for client";
        } else if (state.equals(State.LEAVING)) {
            return "Node '" + this.nodeState.getNodeId() + "' is already in process of leaving the cluster";
        }

        nodeState.setState(State.LEAVING);

        try {
            // Update counter
            nodeState.getMembershipLogger().updateCounter();

            // Send leave message
            MulticastMessageSender.multicastLeave(nodeState.getNodeId(), nodeState.getMembershipLogger().getMembershipCounter(), nodeState.getmCastIpAddress(), nodeState.getmCastPort());
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }

        serviceProvider.stopMembershipService();
        nodeState.setState(State.WAITING_FOR_CLIENT);
        System.out.println("LEFT CLUSTER");

        return "Node '" + nodeState.getNodeId() + "' left cluster successfully";

    }
}

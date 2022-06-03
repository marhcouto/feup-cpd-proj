package store.service;

import requests.multicast.JoinMembershipMessage;
import store.node.NodeState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class LeaveMessageSender implements Runnable {

    private final NodeState nodeState;
    public LeaveMessageSender(NodeState nodeState){
        this.nodeState = nodeState;
    }

    @Override
    public void run(){
        try{
            System.out.println("Sending multicast join message from node: '" + nodeState.getNodeId() + "'");
            MulticastSocket socket;
            socket = new MulticastSocket();
            JoinMembershipMessage joinMessage = new JoinMembershipMessage("1699", nodeState.getMembershipLogger().getMembershipCounter(), nodeState.getNodeId());
            DatagramPacket packet = new DatagramPacket(joinMessage.toString().getBytes(), joinMessage.toString().length(), nodeState.getmCastIpAddress(), nodeState.getmCastPort());
            socket.send(packet);
            socket.close();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}

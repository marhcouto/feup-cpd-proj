package store.service;

import requests.multicast.JoinMembershipMessage;
import requests.multicast.MembershipMessage;
import store.node.NodeState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class JoinMessageSender implements Runnable{

    private final NodeState nodeState;

    /*Maybe change this variable to nodeState*/
    private final String privatePort = "1699";

    public JoinMessageSender(NodeState nodeState){
        this.nodeState = nodeState;
    }

    @Override
    public void run(){
        try{
            System.out.println("Sending multicast join message from node: '" + nodeState.getNodeId() + "'");
            MulticastSocket socket;
            socket = new MulticastSocket();
            JoinMembershipMessage joinMessage = new JoinMembershipMessage(privatePort, nodeState.getMembershipLogger().getMembershipCounter(), nodeState.getNodeId());
            DatagramPacket packet = new DatagramPacket(joinMessage.toString().getBytes(), joinMessage.toString().length(), nodeState.getmCastIpAddress(), nodeState.getmCastPort());
            socket.send(packet);
            socket.close();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}

package store.multicast;

import requests.multicast.JoinMembershipMessage;
import requests.multicast.LeaveMembershipMessage;
import requests.multicast.MembershipMessage;
import store.node.Neighbour;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;

public class MulticastMessageSender {

    public static void multicastLeave(String nodeId, int membershipCounter, InetAddress multicastAddress, int multicastPort) throws IOException {
        MulticastSocket socket = new MulticastSocket();
        LeaveMembershipMessage leaveMessage = new LeaveMembershipMessage(nodeId, membershipCounter);
        DatagramPacket packet = new DatagramPacket(leaveMessage.toString().getBytes(), leaveMessage.toString().length(), multicastAddress, multicastPort);
        socket.send(packet);
        socket.close();
    }

    public static void multicastJoin(String nodeId, int membershipCounter, InetAddress multicastAddress, int multicastPort, int privatePort) throws IOException {
        MulticastSocket socket = new MulticastSocket();
        JoinMembershipMessage joinMessage = new JoinMembershipMessage(Integer.toString(privatePort), membershipCounter, nodeId);
        DatagramPacket packet = new DatagramPacket(joinMessage.toString().getBytes(), joinMessage.toString().length(), multicastAddress, multicastPort);
        socket.send(packet);
        socket.close();
    }

    public static void multicastMembership(List<Neighbour> log, List<Neighbour> activeNodes, String nodeId, InetAddress multicastAddress, int multicastPort) throws IOException {
        MulticastSocket socket = new MulticastSocket();
        MembershipMessage message = new MembershipMessage(log, activeNodes, nodeId);
        DatagramPacket packet = new DatagramPacket(message.toString().getBytes(), message.toString().length(), multicastAddress, multicastPort);
        socket.send(packet);
        socket.close();
    }
}

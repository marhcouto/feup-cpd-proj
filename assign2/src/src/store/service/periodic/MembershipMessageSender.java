package store.service.periodic;

import requests.multicast.MembershipMessage;
import store.node.Neighbour;
import store.node.Node;
import store.node.NodeState;
import store.node.State;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.List;

public class MembershipMessageSender extends PeriodicActor {

    public MembershipMessageSender(NodeState nodeState) {
        super(nodeState);
    }

    @Override
    protected long getInterval() { return 1; }

    @Override
    public void run() {
        if (!shouldSendMembership()) return;
        System.out.println("MULTICASTING MEMBERSHIP");
        try {
            MulticastSocket socket;
            socket = new MulticastSocket();
            MembershipMessage message = new MembershipMessage(nodeState.getMembershipLogger().getLog(), nodeState.getMembershipLogger().getActiveNodes());
            DatagramPacket packet = new DatagramPacket(message.toString().getBytes(), message.toString().length(), nodeState.getmCastIpAddress(), nodeState.getmCastPort());
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean shouldSendMembership() {
        List<Neighbour> activeNodes = this.nodeState.getMembershipLogger().getActiveNodes();
        if (activeNodes.isEmpty()) {
            return false;
        }
        Node node1 = nodeState;
        Node node2 = activeNodes.get(0);
        return (node1.equals(node2) && nodeState.getState().equals(State.JOINED));
    }
}

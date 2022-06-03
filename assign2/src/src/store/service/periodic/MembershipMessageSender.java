package store.service.periodic;

import requests.multicast.MembershipMessage;
import store.node.Neighbour;
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
        return (!activeNodes.isEmpty() && activeNodes.get(0).equals(nodeState) && nodeState.getState() == State.JOINED);
    }
}

package store.service;

import store.handlers.multicast.DispatchMulticastMessage;
import store.node.NodeState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MembershipServiceThread extends Thread {

    ExecutorService requestDispatchers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    private final NodeState nodeState;

    public MembershipServiceThread(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    @Override
    public void run() {
        try {
            MulticastSocket socket = new MulticastSocket(nodeState.getmCastPort());
            socket.joinGroup(nodeState.getmCastIpAddress());
            while(!Thread.interrupted()) {
                byte[] buffer = new byte[100];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                requestDispatchers.execute(new DispatchMulticastMessage(nodeState, packet));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

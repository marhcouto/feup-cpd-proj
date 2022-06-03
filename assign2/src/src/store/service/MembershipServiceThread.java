package store.service;

import store.handlers.membership.DispatchMulticastMessage;
import store.node.Node;
import store.node.NodeState;
import store.service.periodic.LogUpdater;
import store.service.periodic.PeriodicActor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MembershipServiceThread extends Thread {

    ExecutorService requestDispatchers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    private final NodeState nodeState;
    private final List<PeriodicActor> periodicActors;

    private MembershipServiceThread(NodeState nodeState, List<PeriodicActor> periodicActors) {
        this.nodeState = nodeState;
        this.periodicActors = periodicActors;
    }

    public static MembershipServiceThread fromState(NodeState nodeState) {
        List<PeriodicActor> actors = List.of(
            new LogUpdater(nodeState)
        );
        return new MembershipServiceThread(nodeState, actors);
    }

    @Override
    public synchronized void start() {
        super.start();
        periodicActors.forEach(PeriodicActor::schedule);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        requestDispatchers.shutdown();
        try {
            if (!requestDispatchers.awaitTermination(5, TimeUnit.SECONDS)) {
                requestDispatchers.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            requestDispatchers.shutdownNow();
        }
        periodicActors.forEach(PeriodicActor::stopExecution);
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

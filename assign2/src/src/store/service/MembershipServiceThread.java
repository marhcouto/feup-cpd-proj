package store.service;

import store.handlers.membership.DispatchMulticastMessage;
import store.node.NodeState;
import store.service.periodic.LogUpdater;
import store.service.periodic.MembershipMessageUpdater;
import store.service.periodic.PeriodicActor;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MembershipServiceThread extends ServiceThread {


    private MembershipServiceThread(NodeState nodeState, List<PeriodicActor> periodicActors) {
        super(nodeState, periodicActors);
        this.requestDispatchers = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 4, Runtime.getRuntime().availableProcessors() * 4,
                0L,TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
    }

    public static MembershipServiceThread fromNode(NodeState nodeState) {
        List<PeriodicActor> actors = new ArrayList<>();
        actors.add(new LogUpdater(nodeState));
        actors.add(new MembershipMessageUpdater(nodeState));
        return new MembershipServiceThread(nodeState, actors);
    }

    @Override
    public void run() {
        System.out.println("MEMBERSHIP-SERVICE: start, listening to port " + nodeState.getmCastPort());
        try {
            MulticastSocket socket = new MulticastSocket(nodeState.getmCastPort());
            socket.joinGroup(nodeState.getmCastIpAddress());
            while(!Thread.interrupted()) {
                byte[] buffer = new byte[100];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                InputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength()));
                requestDispatchers.execute(new DispatchMulticastMessage(nodeState, inputStream));
            }
            requestDispatchers.shutdown();
            requestDispatchers.awaitTermination(5, TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            requestDispatchers.shutdownNow();
        }
    }
}

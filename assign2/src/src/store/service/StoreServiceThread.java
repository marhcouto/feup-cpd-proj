package store.service;

import store.node.NodeState;
import store.handlers.store.DispatchStoreRequest;
import store.node.State;
import store.service.periodic.CheckReplicationFactor;
import store.service.periodic.PeriodicActor;
import store.service.periodic.PeriodicDeleteTombstones;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StoreServiceThread extends Thread {
    ExecutorService requestDispatchers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    private ServerSocket serverSocket;
    private final NodeState nodeState;
    private List<PeriodicActor> periodicActors;

    private StoreServiceThread(NodeState nodeState, List<PeriodicActor> periodicActors) {
        this.nodeState = nodeState;
        this.periodicActors = periodicActors;
    }

    public static StoreServiceThread fromState(NodeState state) {
        List<PeriodicActor> periodicActorList = List.of(
            new CheckReplicationFactor(state),
            new PeriodicDeleteTombstones(state)
        );
        return new StoreServiceThread(state, periodicActorList);
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
            if(!requestDispatchers.awaitTermination(5, TimeUnit.SECONDS)) {
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
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(nodeState.getTcpDataConnectionAddress().getHostString(), nodeState.getTcpDataConnectionAddress().getPort()));
            while(!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                System.out.println(String.format("Received Request from: %s", socket.getRemoteSocketAddress().toString()));
                switch (nodeState.getState()) {
                    case WAITING_FOR_CLIENT -> socket.getOutputStream().write("Please join first!".getBytes(StandardCharsets.US_ASCII));
                    case JOINING -> socket.getOutputStream().write("Please wait for the node to join!".getBytes(StandardCharsets.US_ASCII));
                    case LEAVING -> socket.getOutputStream().write("The node is leaving you will have to join later".getBytes(StandardCharsets.US_ASCII));
                    default -> requestDispatchers.execute(new DispatchStoreRequest(nodeState, socket));
                }
                socket.close();
                System.out.println(String.format("Handled Request from: %s", socket.getRemoteSocketAddress().toString()));
            }
            requestDispatchers.shutdown();
            requestDispatchers.awaitTermination(5, TimeUnit.SECONDS);
        } catch (IOException e) {
            System.out.println("Failed to open TCP server socket to listen to clients");
            e.printStackTrace();
        } catch (CancellationException e) {
            System.out.println("Closing TCP server with the clients");
            e.printStackTrace();
        } catch (InterruptedException e) {
            requestDispatchers.shutdownNow();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing TCP server with the clients");
            }
        }
    }
}

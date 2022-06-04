package store.service;

import store.node.NodeState;
import store.handlers.store.DispatchStoreRequest;
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

public class StoreServiceThread extends ServiceThread {
    private ServerSocket serverSocket;
    private StoreServiceThread(NodeState nodeState, List<PeriodicActor> periodicActors) {
        super(nodeState, periodicActors);
        this.requestDispatchers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    }

    public static StoreServiceThread fromNode(NodeState state) {
        List<PeriodicActor> periodicActorList = List.of(
            new CheckReplicationFactor(state),
            new PeriodicDeleteTombstones(state)
        );
        return new StoreServiceThread(state, periodicActorList);
    }

    @Override
    public void run() {
        System.out.println("STORE-SERVICE: start, listening to port " + nodeState.getTcpDataConnectionAddress().getPort());
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(nodeState.getTcpDataConnectionAddress().getHostString(), nodeState.getTcpDataConnectionAddress().getPort()));
            while(!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                switch (nodeState.getState()) {
                    case WAITING_FOR_CLIENT -> {
                        socket.getOutputStream().write("Please join first!".getBytes(StandardCharsets.US_ASCII));
                        socket.close();
                    }
                    case JOINING -> {
                        socket.getOutputStream().write("Please wait for the node to join!".getBytes(StandardCharsets.US_ASCII));
                        socket.close();
                    }
                    case LEAVING -> {
                        socket.getOutputStream().write("The node is leaving you will have to join later!".getBytes(StandardCharsets.US_ASCII));
                        socket.close();
                    }
                    default -> requestDispatchers.execute(new DispatchStoreRequest(nodeState, socket));
                }
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

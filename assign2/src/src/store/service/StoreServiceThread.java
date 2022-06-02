package store.service;

import store.node.NodeState;
import store.handlers.store.DispatchStoreRequest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreServiceThread extends Thread {
    ExecutorService requestDispatchers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    private ServerSocket serverSocket;
    private final NodeState nodeState;


    public StoreServiceThread(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(nodeState.getTcpDataConnectionAddress().getHostString(), nodeState.getTcpDataConnectionAddress().getPort()));
            while(!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                System.out.println(String.format("Received Request from: %s", socket.getRemoteSocketAddress().toString()));
                requestDispatchers.execute(new DispatchStoreRequest(nodeState, socket));
                System.out.println(String.format("Handled Request from: %s", socket.getRemoteSocketAddress().toString()));
            }
        } catch (IOException e) {
            System.out.println("Failed to open TCP server socket to listen to clients");
            e.printStackTrace();
        } catch (CancellationException e) {
            System.out.println("Closing TCP server with the clients");
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing TCP server with the clients");
            }
        }
    }
}

package store.service;

import store.NodeState;
import store.requests.DispatchClientRequestTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataServiceThread extends Thread {
    ExecutorService requestDispatchers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    private ServerSocket serverSocket;
    private final NodeState nodeState;


    public DataServiceThread(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(nodeState.getTcpDataConnectionAddress().getHostString(), nodeState.getTcpDataConnectionAddress().getPort()));
            while(!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                System.out.println("Received new connection");
                requestDispatchers.execute(new DispatchClientRequestTask(nodeState, socket));
            }
        } catch (IOException e) {
            System.out.println("Failed to open TCP server socket to listen to clients");
        } catch (CancellationException e) {
            System.out.println("Closing TCP server with the clients");
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing TCP server with the clients");
            }
        }
    }
}

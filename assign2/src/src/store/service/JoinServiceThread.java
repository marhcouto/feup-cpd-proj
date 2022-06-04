package store.service;

import requests.NetworkSerializable;
import requests.multicast.MembershipMessage;
import store.handlers.membership.DispatchMulticastMessage;
import store.handlers.store.DispatchStoreRequest;
import store.node.Node;
import store.node.NodeState;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JoinServiceThread extends Thread {

    /*Maybe refactor the following 3 declarations into constants*/
    ExecutorService requestDispatchers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    private boolean isPortOpen = false;
    private static final int port = 1699;
    public static final int MAX_CONNECTIONS = 3;
    public static final int MAX_TIMEOUT = 10000; /*25 seconds*/
    private ServerSocket serverSocket;
    private final NodeState nodeState;
    private int flag = 0;
    private int connectionsEstablished = 0;

    public JoinServiceThread(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    public synchronized boolean getPortStatus(){
        return this.isPortOpen;
    }

    public void incrementConnectionCounter(){
        this.connectionsEstablished++;
    }

    public int getConnectionsEstablished(){
        return this.connectionsEstablished;
    }

    public void timeout(ServerSocket serverSocket) throws IOException{
        long start = System.currentTimeMillis();
        long end = start + 3000;

        while(System.currentTimeMillis() < end);

        if(!serverSocket.isClosed()){
            System.out.println("Timeout reached, closing server Socket");
            flag = 1;
            serverSocket.close();
        }
    }

    public int getPort() { return port; }

    public Thread timeoutThread(){
        return new Thread(() -> {
            try {
                timeout(serverSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void run(){
        try{
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(nodeState.getNodeId(), port));
            this.isPortOpen = true;
            timeoutThread().start();
            System.out.println("JOIN-PROCESS: Opening TCP private connection on port: " + port);
            while (!Thread.interrupted() && connectionsEstablished != MAX_CONNECTIONS) {
                Socket socket = serverSocket.accept();
                requestDispatchers.execute(new DispatchMulticastMessage(nodeState, socket.getInputStream()));
                incrementConnectionCounter();
                /* Need to dispatch log executor, get the socket content and update local logs*/
            }
        } catch (IOException e) {
            if (flag == 1) System.out.println("Not enough connections were established during join protocol");
            else System.out.println("Failed to open TCP server socket to listen to clients");
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

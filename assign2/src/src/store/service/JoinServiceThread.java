package store.service;

import requests.NetworkSerializable;
import requests.multicast.MembershipMessage;
import store.handlers.store.DispatchStoreRequest;
import store.node.Node;
import store.node.NodeState;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JoinServiceThread extends Thread {

    /*Maybe refactor the following 3 declarations into constants*/
    private boolean isPortOpen;
    public static final int MAX_CONNECTIONS = 3;
    public static final int MAX_TIMEOUT = 10000; /*25 seconds*/
    private ServerSocket serverSocket;
    private final NodeState nodeState;
    private int flag = 0;
    private int connectionsEstablished;
    private final int port = 1699;



    ExecutorService requestDispatchers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);


    public JoinServiceThread(NodeState nodeState) {
        this.isPortOpen = false;
        this.nodeState = nodeState;
        this.connectionsEstablished = 0;
    }

    private void setPortOpen(){
        System.out.println("TCP private connection for node '" + nodeState.getNodeId() + "' on port: 1699 available");
        this.isPortOpen = true;
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
            System.out.println("Timeout reached closing server Socket");
            flag = 1;
            serverSocket.close();
        }
    }

    public int getPort() { return this.port; }

    public Thread timeoutThread(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    timeout(serverSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void run(){
        try{
            /*
                refactor to serverSocket(0);
                getLocalPort();
             */
            System.out.println("Opening TCP private connection for node '" + nodeState.getNodeId() + "' on port: 1699");
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(nodeState.getNodeId(), this.port));
            setPortOpen();
            /* Refactor to future, but this solution works just fine now */
            timeoutThread().start();

            while (!Thread.interrupted() && connectionsEstablished != MAX_CONNECTIONS) {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                String[] headers = NetworkSerializable.getHeader(inputStream);
                MembershipMessage.processMessage(this.nodeState, inputStream);
                incrementConnectionCounter();
                System.out.println("Received new connection");
                System.out.println("Socket content: " + socket.getInputStream());
                /* Need to dispatch log executor, get the socket content and update local logs*/
                //requestDispatchers.execute(new DispatchStoreRequest(nodeState, socket));
            }
            System.out.println("Node with id '" + nodeState.getNodeId() + "' joined the cluster successfully");
            System.out.println("Closed Socket");
        } catch (IOException e) {
            if(flag == 1) System.out.println("Not enough connections where established during join protocol");
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

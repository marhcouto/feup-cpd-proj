package store.requests;

import requests.NetworkSerializable;
import requests.RequestType;
import requests.exceptions.InvalidByteArray;
import store.NodeState;

import java.io.*;
import java.net.Socket;

public class DispatchClientRequestTask implements Runnable {
    private final Socket clientSocket;
    private final NodeState nodeState;

    public DispatchClientRequestTask(NodeState nodeState, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.nodeState = nodeState;
    }

    private RequestHandler getRequestHandler(InputStream messageBuffer) throws IOException, InvalidByteArray {
        String requestType = NetworkSerializable.readLine(messageBuffer);
        switch (requestType) {
            case RequestType.PUT -> {
                return new PutRequestHandler(nodeState);
            }
            default -> throw new InvalidByteArray("Request type not recognized");
        }
    }

    @Override
    public void run() {
        try {
            RequestHandler requestHandler = getRequestHandler(clientSocket.getInputStream());
            clientSocket.close();
        } catch (InvalidByteArray e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error communicating with client");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket");
            }
        }
    }
}

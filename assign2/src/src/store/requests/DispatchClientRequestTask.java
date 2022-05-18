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
    private InputStream is;

    public DispatchClientRequestTask(NodeState nodeState, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.nodeState = nodeState;
    }

    private RequestHandler getRequestHandler(String requestType) throws IOException, InvalidByteArray {
        switch (requestType) {
            case RequestType.PUT -> {
                return new PutRequestHandler(nodeState, clientSocket.getOutputStream(), is);
            }
            case RequestType.GET -> {
                return null;
            }
            default -> throw new InvalidByteArray("Request type not recognized");
        }
    }

    @Override
    public void run() {
        try {
            is = clientSocket.getInputStream();
            String[] headers = NetworkSerializable.getLines(is);
            RequestHandler requestHandler = getRequestHandler(headers[0]);
            requestHandler.execute(headers);
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

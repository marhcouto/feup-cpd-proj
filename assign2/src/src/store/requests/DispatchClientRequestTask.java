package store.requests;

import requests.NetworkSerializable;
import requests.RequestType;
import requests.exceptions.InvalidByteArray;
import store.state.NodeState;

import java.io.*;
import java.net.Socket;

public class DispatchClientRequestTask implements Runnable {
    private final Socket clientSocket;
    private final NodeState nodeState;

    public DispatchClientRequestTask(NodeState nodeState, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.nodeState = nodeState;
    }

    private RequestHandler getRequestHandler(String requestType) throws IOException, InvalidByteArray {
        switch (requestType) {
            case RequestType.PUT -> {
                return new PutRequestHandler(nodeState);
            }
            case RequestType.GET -> {
                return new GetRequestHandler(nodeState);
            }
            case RequestType.DELETE -> {
                return new DeleteRequestHandler(nodeState);
            }
            default -> throw new InvalidByteArray("Request type not recognized");
        }
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            String[] headers = NetworkSerializable.getHeader(inputStream);
            RequestHandler requestHandler = getRequestHandler(headers[0]);
            requestHandler.execute(headers, clientSocket.getOutputStream(), inputStream);
        } catch (InvalidByteArray e) {
            System.out.println("Message received from the client is invalid");
            e.printStackTrace();
        } catch (IOException e) {
            //We use the message from the exception because this exception is general, and we cannot tell what was the error
            System.out.println(e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error closing client socket");
            }
        }
    }
}

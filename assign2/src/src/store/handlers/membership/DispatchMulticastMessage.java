package store.handlers.membership;

import requests.NetworkSerializable;
import requests.RequestType;
import requests.exceptions.InvalidByteArray;
import store.node.NodeState;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class DispatchMulticastMessage implements Runnable {
    private final NodeState nodeState;
    private final InputStream inputStream;
    public DispatchMulticastMessage(NodeState nodeState, InputStream inputStream) {
        this.nodeState = nodeState;
        this.inputStream = inputStream;
    }

    private MulticastMessageHandler getRequestHandler(String requestType) throws IOException, InvalidByteArray {
        switch (requestType) {
            case RequestType.MEMBERSHIP -> {
                return new MembershipMessageHandler(nodeState);
            }
            case RequestType.JOIN -> {
                return new JoinRequestHandler(nodeState);
            }
            case RequestType.LEAVE -> {
                return new LeaveRequestHandler(nodeState);
            }
            default -> throw new InvalidByteArray("Request type not recognized");
        }
    }


    @Override
    public void run() {
        try {
            String[] headers = NetworkSerializable.getHeader(inputStream);
            assert headers != null;
            MulticastMessageHandler handler = this.getRequestHandler(headers[0]);
            handler.execute(headers, inputStream);
        } catch (IOException | InvalidByteArray e) {
            // TODO: get better exception handling
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

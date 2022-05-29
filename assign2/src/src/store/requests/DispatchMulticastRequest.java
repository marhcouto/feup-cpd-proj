package store.requests;

import requests.NetworkSerializable;
import requests.RequestType;
import requests.exceptions.InvalidByteArray;
import store.state.NodeState;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

public class DispatchMulticastRequest implements Runnable {
    //TODO: Organize packages
    private final NodeState nodeState;
    private final DatagramPacket packet;
    public DispatchMulticastRequest(NodeState nodeState, DatagramPacket packet) {
        this.nodeState = nodeState;
        this.packet = packet;
    }

    private MulticastMessageHandler getRequestHandler(String requestType) throws IOException, InvalidByteArray {
        switch (requestType) {
            case RequestType.MEMBERSHIP -> {
                return new MembershipMessageHandler(nodeState);
            }
            case RequestType.JOIN -> {
                // TODO: implement this
                return null;
            }
            default -> throw new InvalidByteArray("Request type not recognized");
        }
    }


    @Override
    public void run() {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength()));
        try {
            String[] headers = NetworkSerializable.getHeader(inputStream);
            assert headers != null;
            MulticastMessageHandler handler = this.getRequestHandler(headers[0]);
            handler.execute(headers, inputStream);
        } catch (IOException | InvalidByteArray e) {
            // TODO: get better exception handling
            throw new RuntimeException(e);
        }
    }
}

package store.handlers.membership;

import requests.NetworkSerializable;
import requests.RequestType;
import requests.exceptions.InvalidByteArray;
import store.node.NodeState;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

public class DispatchMulticastMessage implements Runnable {
    private final NodeState nodeState;
    private final DatagramPacket packet;
    public DispatchMulticastMessage(NodeState nodeState, DatagramPacket packet) {
        this.nodeState = nodeState;
        this.packet = packet;
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

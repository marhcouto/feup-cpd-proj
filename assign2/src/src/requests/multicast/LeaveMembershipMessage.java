package requests.multicast;

import requests.NetworkSerializable;
import requests.RequestType;
import store.node.Neighbour;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LeaveMembershipMessage extends NetworkSerializable {

    private final int nodeMembershipCounter;
    private final String nodeId;

    public LeaveMembershipMessage(String nodeId, int nodeMembershipCounter) {
        this.nodeId = nodeId;
        this.nodeMembershipCounter = nodeMembershipCounter;
    }


    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.LEAVE + endOfLine
                + nodeId + endOfLine
                + nodeMembershipCounter + endOfLine
                + endOfLine;

        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));
    }

    @Override
    public String toString() {
        return RequestType.LEAVE + endOfLine
                + nodeId + endOfLine
                + nodeMembershipCounter + endOfLine
                + endOfLine;
    }
}

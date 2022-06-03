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

    public LeaveMembershipMessage(int nodeMembershipCounter) {
        this.nodeMembershipCounter = nodeMembershipCounter;
    }


    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.LEAVE + endOfLine + nodeMembershipCounter + endOfLine
                + endOfLine;

        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));
    }
}

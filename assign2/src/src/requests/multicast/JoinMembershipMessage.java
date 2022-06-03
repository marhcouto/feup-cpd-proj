package requests.multicast;

import requests.NetworkSerializable;
import requests.RequestType;
import store.node.Neighbour;
import store.node.Node;
import store.node.NodeState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JoinMembershipMessage extends NetworkSerializable {

    private final int nodeMembershipCounter;

    private final String nodeId;

    private final String nodePrivatePort;

    public JoinMembershipMessage(String nodePrivatePort, int nodeMembershipCounter, String nodeId) {
        this.nodePrivatePort = nodePrivatePort;
        this.nodeMembershipCounter = nodeMembershipCounter;
        this.nodeId = nodeId;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {

        String header = RequestType.JOIN + endOfLine + nodeMembershipCounter + endOfLine
                + nodePrivatePort + endOfLine + endOfLine;

        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));
    }

    @Override
    public String toString(){
        String header = RequestType.JOIN + endOfLine + endOfLine;
        String privatePort = nodePrivatePort + endOfLine;
        String nodeAp = nodeId + endOfLine;
        String nodeCounter = nodeMembershipCounter + endOfLine;

        return header + privatePort + nodeAp + nodeCounter;
    }

}

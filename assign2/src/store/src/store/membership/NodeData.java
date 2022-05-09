package store.membership;

import store.coms.NetworkSerializable;

import java.util.Objects;

public record NodeData(String nodeId, int listeningPort, int membershipCounter) implements NetworkSerializable {
    public NodeData {
        Objects.requireNonNull(nodeId);
        Objects.requireNonNull(listeningPort);
        Objects.requireNonNull(membershipCounter);
    }

    @Override
    public String toNetworkString() {
        return String.format("%s;%d;%d%s", nodeId, listeningPort, membershipCounter, endOfLine);
    }
}

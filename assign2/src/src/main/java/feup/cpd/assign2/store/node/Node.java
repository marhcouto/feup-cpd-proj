package store.node;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Node {

    protected final String nodeId;

    protected Node(String nodeId) {
        this.nodeId = nodeId;
    }
    public String getNodeId() { return this.nodeId; };
    public BigInteger getHashedNodeId() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new BigInteger(1, digest.digest(getNodeId().getBytes(StandardCharsets.US_ASCII)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Node n)) return false;

        return n.getNodeId().equals(this.getNodeId());
    }
}

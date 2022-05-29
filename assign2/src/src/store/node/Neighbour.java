package store.node;

import store.filesystem.FileStorable;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Neighbour implements FileStorable, Node {
    private final String nodeId;
    private String membershipCounter;

    public Neighbour(String nodeId, String membershipCounter) {
        this.nodeId = nodeId;
        this.membershipCounter = membershipCounter;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    public String getMembershipCounter() {
        return membershipCounter;
    }

    public void updateMembershipCounter(String counter) { this.membershipCounter = counter.toString(); }

    public static Neighbour fromString(String logFileString) {
        String[] elems = logFileString.split("-", 2);
        System.out.println(elems[0]);
        return new Neighbour(elems[0], elems[1]);
    }

    @Override
    public String toFile() {
        return String.format("%s %s", getNodeId(), getMembershipCounter());
    }

    @Override
    public BigInteger getHashedNodeId() {
        // TODO: refactor to an intermediate class between Node and this and NodeState
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new BigInteger(1, digest.digest(getNodeId().getBytes(StandardCharsets.US_ASCII)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return getNodeId() + "-" + getMembershipCounter();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Neighbour)) return false;

        Neighbour n = (Neighbour) o;
        return n.getNodeId().equals(this.getNodeId());
    }
}

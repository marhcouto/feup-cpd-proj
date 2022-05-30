package store.node;

import store.filesystem.FileStorable;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Neighbour extends Node implements FileStorable {
    private String membershipCounter;

    public Neighbour(String nodeId, String membershipCounter) {
        super(nodeId);
        this.membershipCounter = membershipCounter;
    }

    public String getMembershipCounter() {
        return membershipCounter;
    }

    public void updateMembershipCounter(String counter) { this.membershipCounter = counter.toString(); }

    public static Neighbour fromString(String logFileString) {
        String[] elems = logFileString.split("-", 2);
        return new Neighbour(elems[0], elems[1]);
    }

    @Override
    public String toFile() {
        return String.format("%s %s", getNodeId(), getMembershipCounter());
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

package store.membership.filesystem;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Neighbour implements FileStorable {
    private String nodeId;
    private String membershipCounter;

    public Neighbour(String nodeId, String membershipCounter) {
        this.nodeId = nodeId;
        this.membershipCounter = membershipCounter;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getMembershipCounter() {
        return membershipCounter;
    }

    public static Neighbour fromString(String logFileString) {
        String[] elems = logFileString.split(";");
        return new Neighbour(elems[0], elems[1]);
    }

    @Override
    public String toFile() {
        return null;
    }
}

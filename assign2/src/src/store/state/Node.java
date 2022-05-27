package store.state;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public interface Node {
    String getNodeId();
    BigInteger getHashedNodeId();
}

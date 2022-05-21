package store.state;

import java.math.BigInteger;

public record HashedNodeWrapper(String nodeId, BigInteger nodeHash) {}
package store.state;

import requests.NetworkRequest;
import store.State;
import store.membership.filesystem.MembershipLogger;
import store.membership.filesystem.Neighbour;
import utils.InvalidArgumentsException;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/*
    This class represents the state of the current node
 */
public class NodeState {
    public static final int EXPECTED_NUM_ARGS = 4;
    private final State state = State.WAIT;
    private final String nodeId;
    private final InetAddress mCastIpAddress;
    private final int mCastPort;
    private final int storePort;

    private final InetSocketAddress tcpDataConnectionAddress;
    private final MembershipLogger membershipLogger;

    private NodeState(String nodeId, InetAddress mCastIpAddress, int mCastPort, int storePort) throws IOException {
        this.nodeId = nodeId;
        this.mCastIpAddress = mCastIpAddress;
        this.mCastPort = mCastPort;
        this.storePort = storePort;
        tcpDataConnectionAddress = new InetSocketAddress(nodeId, storePort);
        this.membershipLogger = new MembershipLogger(nodeId);
    }

    public static String usage() {
        return "Usage: java store.Store <IP_mcast_addr> <IP_mcast_port> <node_id>  <Store_port>";
    }

    public static NodeState fromArguments(String[] args) throws InvalidArgumentsException, IOException {
        if (args.length != EXPECTED_NUM_ARGS) {
            throw new InvalidArgumentsException(String.format("Expected %d arguments but %d were given", EXPECTED_NUM_ARGS, args.length));
        }
        String nodeId = args[2];
        InetAddress mCastIpAddress;
        int mCastPort;
        int storePort;

        try {
            mCastPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("The mcast port provided is not a valid integer");
        }

        try {
            storePort = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentsException("The store port provided is not a valid integer");
        }

        try {
            mCastIpAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            throw new InvalidArgumentsException("The mcast host provided in the arguments couldn't be found");
        }

        return new NodeState(nodeId, mCastIpAddress, mCastPort, storePort);
    }

    public InetSocketAddress getTcpDataConnectionAddress() {
        return tcpDataConnectionAddress;
    }

    public String getNodeId() {
        return nodeId;
    }

    public List<Neighbour> getNeighbourNodes() {
        //TODO: Implement this function that must return all the neighbours of the current node
        return Arrays.asList(
            new Neighbour("127.0.0.2", "2"),
            new Neighbour("127.0.0.3", "3"),
            new Neighbour("127.0.0.4", "4"),
            new Neighbour("127.0.0.5", "5")
        );
    }

    public String findNearestNeighbour(NetworkRequest request) throws NoSuchAlgorithmException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            List<HashedNodeWrapper> neighbourHashes = getNeighbourNodes().stream().map((neighbour -> { return new HashedNodeWrapper(neighbour.getNodeId(), new BigInteger(1, digest.digest(getNodeId().getBytes(StandardCharsets.UTF_8)))); })).sorted(new Comparator<HashedNodeWrapper>() {
                @Override
                public int compare(HashedNodeWrapper o1, HashedNodeWrapper o2) {
                    return o1.nodeHash().compareTo(o2.nodeHash());
                }
            }).toList();
            BigInteger requestHash = new BigInteger(1, digest.digest(request.getKey().getBytes(StandardCharsets.UTF_8)));
            for (int i = 1; i < neighbourHashes.size(); i++) {
                BigInteger curHash = neighbourHashes.get(i).nodeHash();
                BigInteger antHash = neighbourHashes.get(i - 1).nodeHash();
                if (requestHash.compareTo(curHash) < 0 && requestHash.compareTo(antHash) >= 0) {
                    return neighbourHashes.get(i).nodeId();
                }
            }
            //If it arrives here the circular list was all traversed and the next node is the starting node
            return neighbourHashes.get(0).nodeId();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}

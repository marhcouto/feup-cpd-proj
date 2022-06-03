package store.node;

import store.filesystem.MembershipLogger;
import store.filesystem.FileStorer;
import utils.InvalidArgumentsException;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/*
    This class represents the state of the current node
 */
public class NodeState extends Node {
    public static final int EXPECTED_NUM_ARGS = 4;
    private State state;
    private final InetAddress mCastIpAddress;
    private final int mCastPort;
    private final int storePort;
    private final InetSocketAddress tcpDataConnectionAddress;
    private final MembershipLogger membershipLogger;
    private final FileStorer storeFiles;

    private NodeState(String nodeId, InetAddress mCastIpAddress, int mCastPort, int storePort) throws IOException {
        super(nodeId);
        this.mCastIpAddress = mCastIpAddress;
        this.mCastPort = mCastPort;
        this.storePort = storePort;
        tcpDataConnectionAddress = new InetSocketAddress(nodeId, storePort);
        this.membershipLogger = new MembershipLogger(this);
        this.state = State.WAITING_FOR_CLIENT;
        this.storeFiles = new FileStorer(this);
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

    public State getNodeState(){
        return this.state;
    }

    public void changeNodeState(State state){
        this.state = state;
    }

    public InetSocketAddress getTcpDataConnectionAddress() {
        return tcpDataConnectionAddress;
    }

    public FileStorer getStoreFiles() { return this.storeFiles; }

    public InetAddress getmCastIpAddress() {
        return mCastIpAddress;
    }

    public MembershipLogger getMembershipLogger() {
        return membershipLogger;
    }

    public int getmCastPort() {
        return mCastPort;
    }

    public List<Neighbour> getNeighbourNodes() {
        //TODO: Implement this function that must return all the neighbours of the current node
        LinkedList<Neighbour> neighbours = new LinkedList<>(Arrays.asList(
                new Neighbour("127.0.0.1", "1"),
                new Neighbour("127.0.0.2", "2"),
                new Neighbour("127.0.0.3", "3"),
                new Neighbour("127.0.0.4", "4")
        ));
        neighbours.removeIf(elem -> elem.getNodeId().equals(getNodeId()));
        return neighbours;
    }

    public synchronized State getNodeStateSync(){
        return this.state;
    }
}

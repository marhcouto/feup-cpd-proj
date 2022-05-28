package store.state;

import requests.NetworkRequest;
import requests.PutRequest;
import store.State;
import store.Store;
import store.membership.filesystem.MembershipLogger;
import store.membership.filesystem.Neighbour;
import store.membership.filesystem.StoreFiles;
import utils.InvalidArgumentsException;
import utils.NeighbourhoodAlgorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static utils.FileKeyCalculate.fileToKey;

/*
    This class represents the state of the current node
 */
public class NodeState implements Node {
    public static final int EXPECTED_NUM_ARGS = 4;
    private State state;
    private final String nodeId;
    private final InetAddress mCastIpAddress;
    private final int mCastPort;
    private final int storePort;

    private final InetSocketAddress tcpDataConnectionAddress;
    private final MembershipLogger membershipLogger;
    private final StoreFiles storeFiles;

    private NodeState(String nodeId, InetAddress mCastIpAddress, int mCastPort, int storePort) throws IOException {
        this.nodeId = nodeId;
        this.mCastIpAddress = mCastIpAddress;
        this.mCastPort = mCastPort;
        this.storePort = storePort;
        tcpDataConnectionAddress = new InetSocketAddress(nodeId, storePort);
        this.membershipLogger = new MembershipLogger(nodeId);
        this.state = State.WAITING_FOR_CLIENT;
        this.storeFiles = new StoreFiles(this);
    }

    public State getNodeState(){
        return this.state;
    }

    public void changeNodeState(State state){
        this.state = state;
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

    @Override
    public String getNodeId() {
        return nodeId;
    }

    public StoreFiles getStoreFiles() { return this.storeFiles; }

    @Override
    public BigInteger getHashedNodeId() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new BigInteger(1, digest.digest(getNodeId().getBytes(StandardCharsets.US_ASCII)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
}

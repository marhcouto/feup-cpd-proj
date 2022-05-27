package store.state;

import requests.NetworkRequest;
import requests.PutRequest;
import store.State;
import store.membership.filesystem.MembershipLogger;
import store.membership.filesystem.Neighbour;
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

    private NodeState(String nodeId, InetAddress mCastIpAddress, int mCastPort, int storePort) throws IOException {
        this.nodeId = nodeId;
        this.mCastIpAddress = mCastIpAddress;
        this.mCastPort = mCastPort;
        this.storePort = storePort;
        tcpDataConnectionAddress = new InetSocketAddress(nodeId, storePort);
        this.membershipLogger = new MembershipLogger(nodeId);
        this.state = State.WAITING_FOR_CLIENT;
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

    public List<File> getFiles() {
        List<File> files = new ArrayList<>();
        File dir = new File(String.format("store-persistent-storage/%s", nodeId));
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.getName().equals("membership_counter")) {
                files.add(new File(file.getName()));
            }
        }
        return files;
    }

    public void distributeFiles() throws IOException {
        List<File> files = getFiles();
        for (File file : files) {
            // TODO: use algorithm to find nearest neighbour properly
            String nearestNodeId = new NeighbourhoodAlgorithms(this).findHeir(file.getName());
            String filePath = Paths.get(String.format("store-persistent-storage/%s/%s", nodeId, file.getName())).toString();
            PutRequest request = new PutRequest(fileToKey(new FileInputStream(filePath)), filePath);
            Socket neighbourNode = new Socket(nearestNodeId, getTcpDataConnectionAddress().getPort());
            request.send(neighbourNode.getOutputStream());
            Files.delete(Paths.get(filePath));
            neighbourNode.close();
        }
    }

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

package store;

import store.membership.MembershipProtocol;
import store.membership.filesystem.MembershipLogger;
import utils.InvalidArgumentsException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Node {
    public static final int EXPECTED_NUM_ARGS = 4;
    private String nodeId;
    private InetAddress mCastIpAddress;
    private int mCastPort;
    private int storePort;

    private MembershipLogger membershipLogger;

    private Node(String nodeId, InetAddress mCastIpAddress, int mCastPort, int storePort) throws IOException {
        this.nodeId = nodeId;
        this.mCastIpAddress = mCastIpAddress;
        this.mCastPort = mCastPort;
        this.storePort = storePort;
        this.membershipLogger = new MembershipLogger(nodeId);
    }

    public static String usage() {
        return "Usage: java store.Store <IP_mcast_addr> <IP_mcast_port> <node_id>  <Store_port>";
    }

    public static Node FromArguments(String[] args) throws InvalidArgumentsException, IOException {
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

        return new Node(nodeId, mCastIpAddress, mCastPort, storePort);
    }

    public void join() throws IOException {
        new MembershipProtocol(membershipLogger, mCastIpAddress.getHostAddress(), mCastPort, nodeId, storePort).performJoin();
    }
}

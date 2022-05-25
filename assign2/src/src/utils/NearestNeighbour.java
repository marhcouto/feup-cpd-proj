package utils;

import store.membership.filesystem.Neighbour;
import store.state.HashedNodeWrapper;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class NearestNeighbour {

    public static List<Neighbour> getNeighbourNodes() {
        //TODO: Implement this function that must return all the neighbours of the current node
        return new LinkedList<>(Arrays.asList(
                new Neighbour("127.0.0.1", "1"),
                new Neighbour("127.0.0.2", "2"),
                new Neighbour("127.0.0.3", "3"),
                new Neighbour("127.0.0.4", "4")
        ));
    }

    private static String findCorrectNode(List<HashedNodeWrapper> nodeHashes, BigInteger requestHash) {
        for (int i = 1; i < nodeHashes.size(); i++) {
            BigInteger curHash = nodeHashes.get(i).nodeHash();
            BigInteger antHash = nodeHashes.get(i - 1).nodeHash();
            if (requestHash.compareTo(curHash) < 0 && requestHash.compareTo(antHash) >= 0) {
                return nodeHashes.get(i).nodeId();
            }
        }
        //If it arrives here the circular list was all traversed and the next node is the starting node
        return nodeHashes.get(0).nodeId();
    }


    public static String findNearestNeighbour(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            List<HashedNodeWrapper> neighbourHashes = getNeighbourNodes().stream().map((neighbour -> new HashedNodeWrapper(neighbour.getNodeId(), new BigInteger(1, digest.digest(neighbour.getNodeId().getBytes(StandardCharsets.UTF_8)))))).sorted(new Comparator<HashedNodeWrapper>() {
                @Override
                public int compare(HashedNodeWrapper o1, HashedNodeWrapper o2) {
                    return o1.nodeHash().compareTo(o2.nodeHash());
                }
            }).toList();
            BigInteger requestHash = new BigInteger(1, digest.digest(key.getBytes(StandardCharsets.UTF_8)));
            return findCorrectNode(neighbourHashes, requestHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String findNearestNeighbour(String key, String nodeId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            List<Neighbour> neighbours = getNeighbourNodes();
            neighbours.removeIf(n -> n.getNodeId().equals(nodeId));
            List<HashedNodeWrapper> nodeHashes = neighbours.stream().map((neighbour -> new HashedNodeWrapper(neighbour.getNodeId(), new BigInteger(1, digest.digest(neighbour.getNodeId().getBytes(StandardCharsets.UTF_8)))))).sorted(new Comparator<HashedNodeWrapper>() {
                @Override
                public int compare(HashedNodeWrapper o1, HashedNodeWrapper o2) {
                    return o1.nodeHash().compareTo(o2.nodeHash());
                }
            }).toList();
            BigInteger requestHash = new BigInteger(1, digest.digest(key.getBytes(StandardCharsets.UTF_8)));
            return findCorrectNode(nodeHashes, requestHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}

package utils;

import store.node.Node;
import store.node.NodeState;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class NeighbourhoodAlgorithms {
    private final static int REPLICATION_FACTOR = 3;
    private final NodeState state;

    public NeighbourhoodAlgorithms(NodeState state) {
        this.state = state;
    }

    private String findNearestNeighbour(List<? extends Node> candidates, String fileKey) {
        try {
            List<? extends Node> sortedCandidates = candidates.stream().sorted(Comparator.comparing(Node::getHashedNodeId)).toList();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            BigInteger requestHash = new BigInteger(1, digest.digest(fileKey.getBytes(StandardCharsets.UTF_8)));
            for (int i = 1; i < sortedCandidates.size(); i++) {
                BigInteger curHash = sortedCandidates.get(i).getHashedNodeId();
                BigInteger antHash = sortedCandidates.get(i - 1).getHashedNodeId();
                if (requestHash.compareTo(curHash) < 0 && requestHash.compareTo(antHash) >= 0) {
                    return sortedCandidates.get(i).getNodeId();
                }
            }
            //If it arrives here the circular list was all traversed and the next node is the starting node
            return sortedCandidates.get(0).getNodeId();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> findNNearestNeighbours(List<? extends Node> candidates, String fileKey, int nNeigh) {
        //TODO: Maybe can be merged with findNearestNeighbour but for now it's separated
        List<? extends Node> sortedCandidates = candidates
                .stream()
                .sorted(Comparator.comparing(Node::getHashedNodeId))
                .collect(Collectors.toCollection(ArrayList::new));
        if (sortedCandidates.size() <= nNeigh) {
            //If we want a bigger replication factor than the number of nodes then
            // all nodes should contain a copy of the data
            return sortedCandidates
                    .stream()
                    .map(Node::getNodeId)
                    .toList();
        }
        String firstReplicationNode = findNearestNeighbour(candidates, fileKey);
        System.out.println("Replication Nodes: " + firstReplicationNode);
        int curNodeIdx = -1;
        for (int i = 0; i < sortedCandidates.size(); i++) {
            if (sortedCandidates.get(i).getNodeId().equals(firstReplicationNode)) {
                curNodeIdx = i;
            }
        }
        List<String> replicationNodes = new LinkedList<>();
        while (replicationNodes.size() < nNeigh) {
            replicationNodes.add(sortedCandidates.get(curNodeIdx).getNodeId());
            curNodeIdx = (curNodeIdx + 1) % sortedCandidates.size();
        }
        return replicationNodes;
    }

    public List<String> findReplicationNodes(String fileKey) {
        List<Node> candidates = new LinkedList<>(state.getNeighbourNodes());
        candidates.add(state);
        return findNNearestNeighbours(candidates, fileKey, REPLICATION_FACTOR);
    }

    public List<String> findReplicationHeirs(String fileKey) {
        return findNNearestNeighbours(state.getNeighbourNodes(), fileKey, REPLICATION_FACTOR);
    }

    public String findRequestDest(String key) {
        List<Node> neighbours = new ArrayList<>(state.getNeighbourNodes());
        neighbours.add(state);
        return findNearestNeighbour(neighbours, key);
    }

    public String findHeir(String key) {
        return findNearestNeighbour(state.getNeighbourNodes(), key);
    }
}

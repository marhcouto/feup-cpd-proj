package store.handlers.store;

import store.node.NodeState;
import utils.algorithms.NeighbourhoodAlgorithms;

import java.io.*;

public abstract class StoreRequestHandler {
    private final NodeState nodeState;
    private final NeighbourhoodAlgorithms neighbourhoodAlgorithms;

    public static final int NUM_RETRIES = 3;

    protected StoreRequestHandler(NodeState nodeState) {
        this.nodeState = nodeState;
        this.neighbourhoodAlgorithms = new NeighbourhoodAlgorithms(nodeState);
    }

    protected NodeState getNodeState() {
        return nodeState;
    }

    public NeighbourhoodAlgorithms getNeighbourhoodAlgorithms() {
        return neighbourhoodAlgorithms;
    }

    public abstract void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException;
}

package store.requests;

import store.state.NodeState;
import utils.NeighbourhoodAlgorithms;

import java.io.*;

public abstract class RequestHandler {
    private final NodeState nodeState;
    private final NeighbourhoodAlgorithms neighbourhoodAlgorithms;

    protected RequestHandler(NodeState nodeState) {
        this.nodeState = nodeState;
        this.neighbourhoodAlgorithms = new NeighbourhoodAlgorithms(nodeState);
    }

    protected NodeState getNodeState() {
        return nodeState;
    }

    public NeighbourhoodAlgorithms getNeighbourhoodAlgorithms() {
        return neighbourhoodAlgorithms;
    }

    abstract void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException;
}

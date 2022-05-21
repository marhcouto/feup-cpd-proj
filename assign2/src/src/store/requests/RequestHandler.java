package store.requests;

import store.state.NodeState;

import java.io.*;

public abstract class RequestHandler {
    private final NodeState nodeState;

    public RequestHandler(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    protected NodeState getNodeState() {
        return nodeState;
    }

    abstract void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException;
}

package store.filesystem;

import store.node.NodeState;

import java.io.IOException;

public abstract class NodeFileHandler {

    protected final NodeState nodeState;

    protected NodeFileHandler(NodeState nodeState) throws IOException {
        this.nodeState = nodeState;
        this.build();
    }

    protected abstract void build() throws IOException;

    public NodeState getNodeState() {
        return nodeState;
    }
}

package store.service.periodic;

import store.node.NodeState;

import java.io.IOException;

public class LogUpdater extends PeriodicActor {

    public LogUpdater(NodeState nodeState) {
        super(nodeState);
    }

    @Override
    protected long getInterval() { return 3; }

    @Override
    public void run() {
        try {
            nodeState.getMembershipLogger().updateLogFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package store.service.periodic;

import store.node.NodeState;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogUpdater extends PeriodicActor {

    public LogUpdater(NodeState nodeState) {
        super(nodeState);
    }

    @Override
    protected long getInterval() { return 1; }

    @Override
    public void run() {
        try {
            nodeState.getMembershipLogger().updateLogFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

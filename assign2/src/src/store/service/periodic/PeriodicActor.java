package store.service.periodic;

import store.node.NodeState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class PeriodicActor implements Runnable {
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    protected final NodeState nodeState;

    protected PeriodicActor(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    public void schedule() {
        scheduler.scheduleAtFixedRate(this, 1, getInterval(), TimeUnit.SECONDS);
    }

    protected abstract long getInterval();
}

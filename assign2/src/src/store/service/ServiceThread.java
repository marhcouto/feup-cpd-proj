package store.service;

import store.node.NodeState;
import store.service.periodic.PeriodicActor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class ServiceThread extends Thread {

    protected ExecutorService requestDispatchers;
    protected final NodeState nodeState;
    private final List<PeriodicActor> periodicActors;

    public ServiceThread(NodeState nodeState, List<PeriodicActor> periodicActors) {
        this.nodeState = nodeState;
        this.periodicActors = periodicActors;
    }

    @Override
    public synchronized void start() {
        super.start();
        periodicActors.forEach(PeriodicActor::schedule);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        requestDispatchers.shutdown();
        try {
            if(!requestDispatchers.awaitTermination(5, TimeUnit.SECONDS)) {
                requestDispatchers.shutdownNow();
            }
        } catch (InterruptedException e) {
            requestDispatchers.shutdownNow();
        }
        periodicActors.forEach(PeriodicActor::stopExecution);
    }

}

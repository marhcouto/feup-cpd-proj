package store.service.periodic;

import store.node.NodeState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PeriodicDeleteTombstones extends PeriodicActor {
    private final static long TIME_TO_DELETE = 5000;

    public PeriodicDeleteTombstones(NodeState state) {
        super(state);
    }

    @Override
    protected long getInterval() {
        return 5;
    }

    @Override
    public void run() {
        try {
            Files.walk(Paths.get(nodeState.getFileStorer().getFileFolder())).forEach(path -> {
                try {
                    if (path.getFileName().toString().matches("([a-z]|\\d)*_DEL")) {
                        long deletedAt = Long.parseLong(Files.readAllLines(path).get(0));
                        System.out.println("Delete interval: " + (System.currentTimeMillis() - deletedAt));
                        if ((System.currentTimeMillis() - deletedAt) > TIME_TO_DELETE) {
                            Files.delete(path);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error deleting tombstone: " + path.getFileName());
                }
            });
        } catch (IOException e) {
            System.out.println("Failed deleting tombstones");
        }
    }
}

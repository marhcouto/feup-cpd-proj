package store.service.periodic;

import store.node.NodeState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;

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
        System.out.println("Ran Periodic deletion of tombstones");
        try {
            Files.walk(Paths.get(nodeState.getStoreFiles().getFileFolder())).forEach(path -> {
                try {
                    if (path.getFileName().toString().matches("([a-z]|\\d)*_DEL")) {
                        Long deletedAt = Long.parseLong(Files.readAllLines(path).get(0));
                        System.out.println("Delete inteval: " + Long.toString(System.currentTimeMillis() - deletedAt));
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

package store.filesystem;

import store.node.Neighbour;
import store.node.NodeState;
import utils.NodeNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MembershipLogger extends NodeFileHandler {
    private final List<Neighbour> log = Collections.synchronizedList(new ArrayList<>());
    private final String nodeFsRoot;
    private int membershipCounter;

    public MembershipLogger(NodeState nodeState) throws IOException {
        super(nodeState);
        this.nodeFsRoot = String.format("store-persistent-storage/%s/", nodeState.getNodeId());
        this.build();
    }
    @Override
    protected void build() throws IOException {
        Path path = Paths.get(nodeFsRoot);
        if (!Files.isDirectory(path)) {
            Files.createDirectories(path);
        }

        path = Paths.get(nodeFsRoot + MembershipFiles.MEMBERSHIP_COUNTER);
        this.membershipCounter = 0;
        if (!Files.isRegularFile(path)) {
            Files.createFile(path);
            Files.writeString(path, "0\n");
        } else {
            this.membershipCounter = Integer.parseInt(Files.lines(path)
                    .map(str -> str.split("\n"))
                    .findFirst()
                    .get()[0]);
        }

        path = Paths.get(nodeFsRoot + MembershipFiles.MEMBERSHIP_LOG);
        if (!Files.isRegularFile(path)) {
            Files.createFile(path);
            log.add(new Neighbour(this.nodeState.getNodeId(), String.valueOf(this.membershipCounter)));
            updateLogFile();
        } else {
            Files.lines(path).forEachOrdered(line -> log.add(Neighbour.fromString(line)));
        }
    }

    public int getMembershipCounter() {
        return membershipCounter;
    }

    public List<Neighbour> getLog() {
        int size = log.size();
        return log.subList(Math.max(0, size - 32), size);
    }

    public void updateCounter() throws IOException {
        Path path = Paths.get(nodeFsRoot + MembershipFiles.MEMBERSHIP_COUNTER);
        this.membershipCounter++;
        Files.writeString(path, Integer.valueOf(this.membershipCounter).toString());
        this.addLogEvent(new Neighbour(nodeState.getNodeId(), ((Integer) this.membershipCounter).toString()));
        this.updateLogFile();
    }

    public synchronized void updateLogFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Neighbour n : log)
            stringBuilder.append(n.toString()).append("\n");
        Path path = Paths.get(nodeFsRoot + MembershipFiles.MEMBERSHIP_LOG);
        if (!Files.isRegularFile(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, stringBuilder);
    }

    public synchronized void addLogEvent(Neighbour neighbour) {
        for (Neighbour n : log) {
            if (!n.equals(neighbour)) continue;
            if (Integer.parseInt(n.getMembershipCounter()) < Integer.parseInt(neighbour.getMembershipCounter())) {
                log.remove(n);
                log.add(neighbour);
            }
            return;
        }
        log.add(neighbour);
    }

    public synchronized void updateLog(List<Neighbour> newLog) {
        for (Neighbour n : newLog)
            this.addLogEvent(n);
    }

    public List<Neighbour> getActiveNodes() {
        List<Neighbour> activeNodes = new ArrayList<>();
        for (Neighbour n : log) {
            if (Integer.parseInt(n.getMembershipCounter()) % 2 != 0 && !activeNodes.contains(n)) activeNodes.add(n);
        }
        return activeNodes;
    }

    public int getMembershipCounter(String nodeId) throws NodeNotFoundException {
        for (Neighbour n : log) {
            if (n.getNodeId().equals(nodeId)) return membershipCounter;
        }
        throw new NodeNotFoundException("Node '" + nodeId + "' not found in log");
    }
}

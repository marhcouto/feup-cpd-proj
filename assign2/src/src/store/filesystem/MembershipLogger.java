package store.filesystem;

import store.node.Neighbour;
import store.node.NodeState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        Files.writeString(path, Integer.valueOf(++membershipCounter).toString());
    }

    public void updateLogFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Neighbour n : log)
            stringBuilder.append(n.toString()).append("\n");
        Path path = Paths.get(nodeFsRoot + MembershipFiles.MEMBERSHIP_LOG);
        if (!Files.isRegularFile(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, stringBuilder);
    }

    public void addEventLog(Neighbour neighbour) {
        System.out.println("Ran Event Log");
        for (Neighbour n : log) {
            System.out.println("Node:" + n.toString());
            if (!n.equals(neighbour)) continue;
            System.out.println("FOUND HIM");
            if (Integer.parseInt(n.getMembershipCounter()) < Integer.parseInt(neighbour.getMembershipCounter())) {
                System.out.println("UPDATING");
                log.remove(n);
                log.add(neighbour);
            }
            return;
        }
        log.add(neighbour);
    }

    public void updateLog(List<Neighbour> newLog) {
        for (Neighbour n : newLog)
            this.addEventLog(n);
    }

    public List<Neighbour> getActiveNodes() {
        List<Neighbour> activeNodes = new ArrayList<>();
        for (Neighbour n : log) {
            if (Integer.parseInt(n.getMembershipCounter()) % 2 == 0 && !activeNodes.contains(n)) activeNodes.add(n);
        }
        return activeNodes;
    }
}

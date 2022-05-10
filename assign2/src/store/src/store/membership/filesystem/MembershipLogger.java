package store.membership.filesystem;

import utils.FixedSizeCircularArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MembershipLogger {
    int memoryStoredLogs = 0;
    FixedSizeCircularArray<Log> latestLogs = new FixedSizeCircularArray<>(32);
    private String nodeFsRoot;
    private int membershipCounter;

    public MembershipLogger(String nodeId) throws IOException {
        nodeFsRoot = String.format("store-persistent-storage/%s/", nodeId);
        Path path = Paths.get(nodeFsRoot);
        if (!Files.isDirectory(path)) {
            Files.createDirectories(path);
        }
        path = Paths.get(nodeFsRoot + MembershipFiles.MEMBERSHIP_COUNTER);
        if (!Files.isRegularFile(path)) {
            Files.createFile(path);
            Files.writeString(path, "0\n");
        }
        membershipCounter = Integer.parseInt(Files.lines(path)
                .map(str -> str.split("\n"))
                .findFirst()
                .get()[0]);
        path = Paths.get(nodeFsRoot + MembershipFiles.MEMBERSHIP_LOG);
        if (Files.isRegularFile(path)) {
            Files.lines(path).forEachOrdered(line -> latestLogs.add(Log.fromString(line)));
            System.out.println(latestLogs.toString());
        }
    }

    public int getMembershipCounter() {
        return membershipCounter;
    }

    public void updateCounter() throws IOException {
        Path path = Paths.get(nodeFsRoot + MembershipFiles.MEMBERSHIP_COUNTER);
        Files.writeString(path, Integer.valueOf(++membershipCounter).toString());
    }

    public void storeLog(Log log) throws IOException {
        Path path = Paths.get(nodeFsRoot + MembershipFiles.MEMBERSHIP_LOG);
        if (!Files.isRegularFile(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, String.format("%s\n", log.toFile()), StandardOpenOption.APPEND);
        latestLogs.add(log);
    }
}

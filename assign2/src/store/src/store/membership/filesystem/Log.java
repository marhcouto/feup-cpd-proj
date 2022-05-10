package store.membership.filesystem;

import store.coms.NetworkSerializable;

public class Log implements FileStorable, NetworkSerializable {
    private String nodeIp;
    private String membershipCounter;

    public Log(String nodeIp, String membershipCounter) {
        this.nodeIp = nodeIp;
        this.membershipCounter = membershipCounter;
    }

    @Override
    public String toNetworkString() {
        return String.format("%s;%s%s", nodeIp, membershipCounter, endOfLine);
    }

    public static Log fromString(String logFileString) {
        String[] elems = logFileString.split(";");
        return new Log(elems[0], elems[1]);
    }

    @Override
    public String toFile() {
        return String.format("%s;%s%s", nodeIp, membershipCounter, endOfLine);
    }
}

package store.membership.filesystem;

import requests.NetworkSerializable;
import requests.exceptions.InvalidByteArray;

public class Log extends NetworkSerializable<Log> implements FileStorable {
    private String nodeIp;
    private String membershipCounter;

    public Log(String nodeIp, String membershipCounter) {
        this.nodeIp = nodeIp;
        this.membershipCounter = membershipCounter;
    }

    @Override
    public Log fromNetworkBytes(byte[] networkBytes) throws InvalidByteArray {
        return null;
    }

    @Override
    public byte[] toNetworkBytes() {
        return new byte[0];
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

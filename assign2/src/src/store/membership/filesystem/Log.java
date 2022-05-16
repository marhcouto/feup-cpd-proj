package store.membership.filesystem;

import requests.NetworkSerializable;
import requests.exceptions.InvalidByteArray;

import java.io.BufferedReader;
import java.io.IOException;

public class Log implements FileStorable {
    private String nodeIp;
    private String membershipCounter;

    public Log(String nodeIp, String membershipCounter) {
        this.nodeIp = nodeIp;
        this.membershipCounter = membershipCounter;
    }

    public static Log fromString(String logFileString) {
        String[] elems = logFileString.split(";");
        return new Log(elems[0], elems[1]);
    }

    @Override
    public String toFile() {
        return null;
    }
}

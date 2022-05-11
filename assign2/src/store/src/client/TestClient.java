package client;

import client.mode.*;
import utils.InvalidArgumentsException;

public class TestClient {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("The number of arguments must be at least 2");
            printUsage();
        }
        String nodeAp = args[0];
        if (nodeAp.split(":").length != 2) {
            System.out.println("Invalid node access point");
            printUsage();
        }
        switch (args[1]) {
            case "join" -> new JoinMode(nodeAp).execute();
            case "leave" -> new LeaveMode(nodeAp).execute();
            case "get" -> new GetMode().execute();
            case "put" -> new PutMode().execute();
            case "delete" -> new DeleteMode().execute();
        }
    }

    private static void printUsage() {
        System.out.println("java TestClient <node_ap> <operation> [<opnd>]");
    }
}

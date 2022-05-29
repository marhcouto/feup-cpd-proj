package client.mode;

import rmi.MembershipCommands;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JoinMode implements Mode {
    private final String host;
    private final String service;

    private final String rmiNodeIdentifier;

    public JoinMode(String nodeAp) {
        this.rmiNodeIdentifier = nodeAp;
        String[] apComponents = nodeAp.split(":");
        host = apComponents[0];
        service = apComponents[1];
    }

    public int getNodeIdLastDigit() {
        String nodeId = host;
        var ipLastDigit = nodeId.charAt(nodeId.length() - 1);
        var charToInt = Character.getNumericValue(ipLastDigit);

        return charToInt;
    }

    @Override
    public void execute() {
        try {
            System.out.println("Entered");
            Registry registry = LocateRegistry.getRegistry(host, 1099+getNodeIdLastDigit());
            System.out.println("Why: " + rmiNodeIdentifier);
            MembershipCommands commands = (MembershipCommands) registry.lookup(rmiNodeIdentifier);
            System.out.println(commands.join());
        } catch (Exception e) {
            System.out.println("Couldn't connect to remote");
        }
    }
}

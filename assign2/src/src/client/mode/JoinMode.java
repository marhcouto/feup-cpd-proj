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

    @Override
    public void execute() {
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            MembershipCommands commands = (MembershipCommands) registry.lookup(rmiNodeIdentifier);
            System.out.println(commands.join());
        } catch (Exception e) {
            System.out.println("Couldn't connect to remote");
        }
    }
}

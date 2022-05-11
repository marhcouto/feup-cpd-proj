package client.mode;

import rmi.MembershipCommands;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LeaveMode implements Mode {
    private final String host;
    private final String service;

    public LeaveMode(String nodeAp) {
        String[] apComponent = nodeAp.split(":");
        host = apComponent[0];
        service = apComponent[1];
    }

    @Override
    public void execute() {
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            MembershipCommands commands = (MembershipCommands) registry.lookup(service);
            System.out.println(commands.leave());
        } catch (Exception e) {
            System.out.println("Couldn't connect to remote");
        }
    }
}

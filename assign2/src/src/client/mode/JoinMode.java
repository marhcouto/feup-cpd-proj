package client.mode;

import rmi.MembershipCommands;
import utils.RmiUtils;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JoinMode extends RmiUtils implements Mode {

    public JoinMode(String nodeAp) {
        super(nodeAp, nodeAp.split(":")[0]);
    }

    @Override
    public void execute() {
        try {
            Registry registry = LocateRegistry.getRegistry(this.getHost(), this.getNodeIdLastDigit());
            MembershipCommands commands = (MembershipCommands) registry.lookup(this.getRmiNodeIdentifier());
            System.out.println(commands.join());
        } catch (Exception e) {
            System.out.println("Couldn't connect to remote");
        }
    }
}

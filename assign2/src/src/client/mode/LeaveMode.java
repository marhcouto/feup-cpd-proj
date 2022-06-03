package client.mode;

import rmi.MembershipCommands;
import utils.RmiUtils;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LeaveMode extends RmiUtils implements Mode {

    public LeaveMode(String nodeAp) {
        super(nodeAp, nodeAp.split(":")[0]);
    }

    @Override
    public void execute() {
        try {
            Registry registry = LocateRegistry.getRegistry(this.getHost(), this.getNodeIdLastDigit());
            MembershipCommands commands = (MembershipCommands) registry.lookup(this.getRmiNodeIdentifier());
            System.out.println(commands.leave());
        } catch (Exception e) {
            System.out.println("Couldn't connect to remote");
        }
    }
}

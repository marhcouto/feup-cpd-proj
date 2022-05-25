package store.coms.client.rmi;

import rmi.MembershipCommands;

import java.rmi.RemoteException;

public class MembershipProtocolRemote implements MembershipCommands {
    @Override
    public String join() throws RemoteException {
        return "Membership Protocol for multicast join RMI";
    }

    @Override
    public String leave() throws RemoteException {
        return "Membership Protocol for multicast leave RMI";
    }
}

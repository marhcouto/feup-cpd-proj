package store.service;

import rmi.MembershipCommands;
import rmi.RMIConstants;
import store.state.NodeState;
import store.coms.client.rmi.MembershipProtocolRemote;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class StoreServiceProvider {
    private final NodeState nodeState;

    public StoreServiceProvider(NodeState store) {
        this.nodeState = store;
    }

    public void setupConnectionService() throws RemoteException, AlreadyBoundException {
        MembershipProtocolRemote obj = new MembershipProtocolRemote();
        MembershipCommands stub = (MembershipCommands) UnicastRemoteObject.exportObject(obj, 0);

        Registry registry = LocateRegistry.getRegistry();
        registry.bind(RMIConstants.SERVICE_NAME, stub);

        Signal.handle(new Signal("TERM"), new SignalHandler() {
            @Override
            public void handle(Signal sig) {
                System.out.println("Terminated");
                System.exit(0);
            }
        });
    }

    public void setupDataService() throws IOException {
        new DataServiceThread(nodeState).start();
    }
}

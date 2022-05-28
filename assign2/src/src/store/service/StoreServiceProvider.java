package store.service;

import rmi.MembershipCommands;
import rmi.RMIConstants;
import store.state.NodeState;
import store.coms.client.rmi.MembershipProtocolRemote;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class StoreServiceProvider{
    private final NodeState nodeState;

    private final String rmiNodeIdentifier;

    public StoreServiceProvider(NodeState store) {
        this.nodeState = store;
        this.rmiNodeIdentifier = nodeState.getNodeId() + ":" + RMIConstants.SERVICE_NAME;
    }

    public void setupConnectionService() throws AlreadyBoundException {
        try{
            MembershipProtocolRemote obj = new MembershipProtocolRemote(nodeState);
            MembershipCommands stub = (MembershipCommands) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.bind(rmiNodeIdentifier, stub);
        }  catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }

    public void setupDataService() throws IOException {
        new DataServiceThread(nodeState).start();
    }
}

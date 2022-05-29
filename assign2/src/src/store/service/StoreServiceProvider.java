package store.service;

import rmi.MembershipCommands;
import rmi.RMIConstants;
import store.state.NodeState;
import store.coms.client.rmi.MembershipProtocolRemote;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class StoreServiceProvider{
    private final NodeState nodeState;

    /*Rmi register identifier equals to nodeap:MemberShipService so that each node can have different bindings*/
    private final String rmiNodeIdentifier;

    public StoreServiceProvider(NodeState store) {
        this.nodeState = store;
        this.rmiNodeIdentifier = nodeState.getNodeId() + ":" + RMIConstants.SERVICE_NAME;
    }

    public int getNodeIdLastDigit() {
        String nodeId = nodeState.getNodeId();
        var ipLastDigit = nodeId.charAt(nodeId.length() - 1);
        var charToInt = Character.getNumericValue(ipLastDigit);

        return charToInt;
    }


    public void setupConnectionService() throws AlreadyBoundException {
        try{
            MembershipProtocolRemote obj = new MembershipProtocolRemote(nodeState);
            MembershipCommands stub = (MembershipCommands) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry = LocateRegistry.createRegistry(1099 + getNodeIdLastDigit());
            System.out.println(rmiNodeIdentifier);

            registry.bind(rmiNodeIdentifier, stub);
        }  catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }

    public void setupDataService() throws IOException {
        new DataServiceThread(nodeState).start();
    }

    public void setupMembershipService() throws IOException {
        new MembershipServiceThread(nodeState).start();
    }
}

package store.service;

import rmi.MembershipCommands;
import rmi.RMIConstants;
import store.state.NodeState;
import store.coms.client.rmi.MembershipProtocolRemote;
import utils.RmiUtils;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class StoreServiceProvider extends RmiUtils {
    private final NodeState nodeState;

    /*Rmi register identifier equals to nodeap:MemberShipService so that each node can have different bindings*/

    public StoreServiceProvider(NodeState store) {
        super(store.getNodeId() + ":" + RMIConstants.SERVICE_NAME, store.getNodeId());
        this.nodeState = store;
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

            Registry registry = LocateRegistry.createRegistry(this.getNodeIdLastDigit());
            registry.bind(this.getRmiNodeIdentifier(), stub);
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

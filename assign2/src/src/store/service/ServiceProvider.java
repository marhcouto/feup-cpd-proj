package store.service;

import rmi.MembershipCommands;
import rmi.RMIConstants;
import store.node.NodeState;
import store.rmi.MembershipProtocolRemote;
import utils.RmiUtils;

import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServiceProvider extends RmiUtils {
    private final NodeState nodeState;
    private StoreServiceThread storeServiceThread = null;
    private MembershipServiceThread membershipServiceThread = null;

    /*Rmi register identifier equals to nodeap:MemberShipService so that each node can have different bindings*/

    public ServiceProvider(NodeState store) {
        super(store.getNodeId() + ":" + RMIConstants.SERVICE_NAME, store.getNodeId());
        this.nodeState = store;
    }

    public void setupConnectionService() throws AlreadyBoundException {
        try{
            MembershipProtocolRemote obj = new MembershipProtocolRemote(nodeState, this);
            MembershipCommands stub = (MembershipCommands) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.createRegistry(this.getNodeIdLastDigit());
            registry.bind(this.getRmiNodeIdentifier(), stub);
        }  catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }

    public void setupDataService() {
        storeServiceThread = StoreServiceThread.fromNode(nodeState);
        storeServiceThread.start();
    }

    public void setupMembershipService() {
        membershipServiceThread = MembershipServiceThread.fromNode(nodeState);
        membershipServiceThread.start();
    }

    public void stopDataService() {
        storeServiceThread.interrupt();
        storeServiceThread = null;
    }

    public void stopMembershipService() {
        membershipServiceThread.interrupt();
    }
}

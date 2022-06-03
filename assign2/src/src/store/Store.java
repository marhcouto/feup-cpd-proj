package store;

import store.service.ServiceProvider;
import store.node.NodeState;
import utils.InvalidArgumentsException;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public class Store {
    public static String usage() {
        return "Usage: java store.Store <IP_mcast_addr> <IP_mcast_port> <node_id>  <Store_port>";
    }
    public static void main(String[] args) throws IOException {
        try {
            ServiceProvider provider = new ServiceProvider(NodeState.fromArguments(args));
            provider.setupConnectionService();
        } catch (InvalidArgumentsException invalidArgumentsException) {
            System.out.println(usage());
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
        try {
            // 292 billion years seems enough
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) { /* Just stop the program */}
    }
}

package store;

import store.service.StoreServiceProvider;
import store.state.NodeState;
import utils.InvalidArgumentsException;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public class Store {
    public static void main(String[] args) throws IOException, RemoteException {
        try {
            StoreServiceProvider provider = new StoreServiceProvider(NodeState.fromArguments(args));
            //provider.setupConnectionService();
            provider.setupDataService();
        } catch (InvalidArgumentsException invalidArgumentsException) {
            System.out.println(NodeState.usage());
        } /*catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }*/
        try {
            // 292 billion years seems enough
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) { /* Just stop the program */}
    }
}
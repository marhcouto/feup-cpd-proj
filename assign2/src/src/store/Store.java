package store;

import store.service.StoreServiceProvider;
import utils.InvalidArgumentsException;

import java.io.IOException;

public class Store {
    public static void main(String[] args) throws IOException {
        try {
            StoreServiceProvider provider = new StoreServiceProvider(NodeState.fromArguments(args));
            //provider.setupConnectionService();
            provider.setupDataService();
        } catch (InvalidArgumentsException invalidArgumentsException) {
            System.out.println(NodeState.usage());
        }
        try {
            // 292 billion years seems enough
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) { /* Just stop the program */}
    }
}
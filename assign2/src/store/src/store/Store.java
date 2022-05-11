package store;

import utils.InvalidArgumentsException;

import java.io.IOException;

public class Store {
    public static void main(String[] args) throws IOException {
        Node node;
        try {
            node = Node.FromArguments(args);
            node.join();
        } catch (InvalidArgumentsException e) {
            System.out.println(e);
            System.out.println(Node.usage());
        }

    }
}
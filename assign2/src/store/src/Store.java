import store.InvalidArgumentsException;
import store.Node;
import store.membership.filesystem.Log;
import store.membership.filesystem.MembershipLogger;

import java.io.IOException;

public class Store {
    public static void main(String[] args) throws IOException {
        Node node;
        try {
            node = Node.FromArguments(args);
        } catch (InvalidArgumentsException e) {
            System.out.println(e);
            System.out.println(Node.usage());
        }

    }
}
import store.InvalidArgumentsException;
import store.Store;
import store.membership.Log;
import store.membership.MembershipLogger;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Store store;
        MembershipLogger membershipLogger = new MembershipLogger("teste");
        membershipLogger.storeLog(new Log("2345", "1234"));
        try {
            store = Store.FromArguments(args);
        } catch (InvalidArgumentsException e) {
            System.out.println(e);
            System.out.println(Store.usage());
        }
    }
}
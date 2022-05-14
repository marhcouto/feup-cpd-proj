package store.membership;

import store.coms.NetworkSerializable;

public class JoinMessage implements NetworkSerializable {
    private int port;
    private int membershipCounter;

    public JoinMessage(int port, int membershipCounter) {
        this.port = port;
        this.membershipCounter = membershipCounter;
    }

    public static JoinMessage fromNetworkString(String message) {
        // TODO: 09/05/2022 Implement this
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    public int getPort() {
        return port;
    }

    public int getMembershipCounter() {
        return membershipCounter;
    }

    @Override
    public String toNetworkString() {
        return String.format("%d%s%d", membershipCounter, endOfLine, port);
    }
}

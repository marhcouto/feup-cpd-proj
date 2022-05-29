package store.handlers.membership;

import requests.multicast.MembershipMessage;
import store.node.NodeState;

import java.io.IOException;
import java.io.InputStream;

public class MembershipMessageHandler extends MulticastMessageHandler {
    public MembershipMessageHandler(NodeState nodeState) {
        super(nodeState);
    }

    public void execute(String headers[], InputStream inputStream) throws IOException {
        MembershipMessage.processMessage(this.getNodeState(), inputStream);
    }
}

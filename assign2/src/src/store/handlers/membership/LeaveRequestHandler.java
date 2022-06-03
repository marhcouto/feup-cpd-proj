package store.handlers.membership;

import requests.multicast.LeaveMembershipMessage;
import store.node.NodeState;

import java.io.IOException;
import java.io.InputStream;

public class LeaveRequestHandler extends MulticastMessageHandler{

    protected LeaveRequestHandler(NodeState nodeState){
        super(nodeState);
    }

    @Override
    public void execute(String[] headers, InputStream inputStream) throws IOException {

    }
}

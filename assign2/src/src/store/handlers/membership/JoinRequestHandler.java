package store.handlers.membership;


import requests.multicast.MembershipMessage;
import store.node.Neighbour;
import store.node.NodeState;
import store.node.State;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class JoinRequestHandler extends MulticastMessageHandler {

    public JoinRequestHandler(NodeState nodeState){
        super(nodeState);
    }

    @Override
    public void execute(String[] headers, InputStream inputStream) throws IOException {
        String nodeAp = headers[1];
        String privatePort = headers[2];
        String nodeCounter = headers[3];

        if (nodeAp.equals(this.getNodeState().getNodeId())) {
            System.err.println("Join sent to the node itself");
            return;
        }

        if (this.getNodeState().getState().equals(State.WAITING_FOR_CLIENT)){
            System.out.println("Node shouldn't get multicast message");
            return;
        }

        System.out.println("Received" + "multicast JOIN message from node '" + nodeAp + "'");

        Socket socket = new Socket(nodeAp, Integer.parseInt(privatePort));
        OutputStream outputStream = socket.getOutputStream();

        /*Socket is now open*/
        /* Needs to send membership message through the TCP port */
        this.getNodeState().getMembershipLogger().addLogEvent(new Neighbour(nodeAp, nodeCounter));
        MembershipMessage membershipMessage = new MembershipMessage(getNodeState().getMembershipLogger().getLog(), getNodeState().getMembershipLogger().getActiveNodes(), getNodeState().getNodeId());
        membershipMessage.send(outputStream);
        socket.close();
    }
}

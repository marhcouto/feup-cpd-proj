package store.handlers.membership;


import requests.multicast.JoinMembershipMessage;
import requests.multicast.MembershipMessage;
import rmi.RMIConstants;
import store.node.Neighbour;
import store.node.NodeState;
import store.node.State;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class JoinRequestHandler extends MulticastMessageHandler {

    public JoinRequestHandler(NodeState nodeState){
        super(nodeState);
    }

    @Override
    public void execute(String[] headers, InputStream inputStream) throws IOException {
        String nodeAp = headers[1];
        String privatePort = headers[2];
        String nodeCounter = headers[3];

        System.out.println("RECEIVED JOIN");

        if(nodeAp.equals(this.getNodeState().getNodeId())){
            return;
        }
        if(this.getNodeState().getNodeStateSync().equals(State.WAITING_FOR_CLIENT)){
            //if the node is sleeping return /*This should be fixed in multicast join
            System.out.println("Node shouldn't get multicast message");
            //return;
        }

        System.out.println("Node with ID '" + this.getNodeState().getNodeId() + "' received" +
                "multicast JOIN message from node '" + nodeAp + "'");

        Socket socket = new Socket(nodeAp, Integer.parseInt(privatePort));
        OutputStream joiningNodeOutputStream = socket.getOutputStream();

        /*Socket is now opened*/
        /* Needs to send membership message through the TCP port */
        this.getNodeState().getMembershipLogger().addEventLog(new Neighbour(nodeAp, nodeCounter));
        System.out.println("Gonna create membership message");
        MembershipMessage membershipMessage = new MembershipMessage(getNodeState().getMembershipLogger().getLog(), getNodeState().getMembershipLogger().getActiveNodes());
        System.out.println("Created membership message");
        membershipMessage.send(joiningNodeOutputStream);
        socket.close();
    }
}

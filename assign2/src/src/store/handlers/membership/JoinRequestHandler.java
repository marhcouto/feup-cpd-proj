package store.handlers.membership;


import requests.multicast.JoinMembershipMessage;
import rmi.RMIConstants;
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
        try (Scanner scanner = new Scanner(inputStream)) {
            String privatePort = scanner.nextLine();
            String nodeAp = scanner.nextLine();
            String nodeCounter = scanner.nextLine();

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

            Socket joinNode = new Socket(nodeAp, 1699);
            OutputStream joinNodeOutputStream = joinNode.getOutputStream();

            /*Socket is now opened*/
            /* Needs to send membership message through the TCP port */

            JoinMembershipMessage joinMessage = new JoinMembershipMessage("1699",this.getNodeState().getMembershipLogger().getMembershipCounter(), this.getNodeState().getNodeId());

            joinMessage.send(joinNodeOutputStream);

            joinNode.close();

            System.out.println(privatePort);
            System.out.println(nodeAp);
            System.out.println(nodeCounter);
        }
    }
}

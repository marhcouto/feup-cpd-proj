package store.membership;

import store.membership.filesystem.MembershipLogger;
import store.requests.RequestBuilder;
import store.requests.RequestType;
import java.io.IOException;
import java.net.*;

public class MembershipProtocol {
    private static int MEMBERSHIP_TIMEOUT = 5000;
    private ServerSocket membershipSocket;
    private MulticastSocket joinSocket;

    private InetSocketAddress group;

    private int nodeLogMessagesReceived = 0;
    private int mcastJoinMessagesSent = 0;
    private MembershipLogger membershipLogger;

    private JoinMessage joinMessage;

    public MembershipProtocol(MembershipLogger logger, String mcastIp, int mcastPort, String nodeIp, int nodePort) throws IOException {
        membershipSocket = new ServerSocket(nodePort);
        joinSocket = new MulticastSocket();
        group = new InetSocketAddress(mcastIp, mcastPort);
        joinSocket.joinGroup(group, NetworkInterface.getByName("eth0"));
        membershipLogger = logger;
        joinMessage = new JoinMessage(mcastPort, logger.getMembershipCounter());
    }

    public void performJoin() throws IOException{
        membershipSocket.setSoTimeout(MEMBERSHIP_TIMEOUT);
        while(mcastJoinMessagesSent < 3) {
            nodeLogMessagesReceived = 0;
            sendJoinMessage();
            while (nodeLogMessagesReceived < 3) {
                try {
                    Socket socket = membershipSocket.accept();
                    nodeLogMessagesReceived++;
                    new Thread() {
                        @Override
                        public void run() {
                            System.out.println("Received Message");
                        }
                    }.start();
                } catch (SocketTimeoutException e) {
                    break;
                }
            }
        }
    }

    private void sendJoinMessage() throws IOException {
        if (joinSocket == null) {
            return;
        }
        if (mcastJoinMessagesSent >= 3) {
            joinSocket.close();
            joinSocket = null;
            return;
        }
        mcastJoinMessagesSent++;
        byte[] joinRequest = new RequestBuilder(RequestType.JOIN).addBody(joinMessage.toNetworkString()).toBytes();
        DatagramPacket msgPacket = new DatagramPacket(joinRequest, joinRequest.length, group.getAddress(), group.getPort());
        joinSocket.send(msgPacket);
    }
}

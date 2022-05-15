package store.membership;

import store.membership.filesystem.MembershipLogger;
import requests.RequestBuilder;
import requests.RequestType;
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

    public MembershipProtocol(MembershipLogger logger, String mcastIp, int mcastPort, String nodeIp, int nodePort) throws IOException {
        membershipSocket = new ServerSocket(nodePort);
        joinSocket = new MulticastSocket();
        group = new InetSocketAddress(mcastIp, mcastPort);
        joinSocket.joinGroup(group, NetworkInterface.getByName("eth0"));
        membershipLogger = logger;
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
                    new Thread(() -> {
                        
                    }).start();
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
    }
}

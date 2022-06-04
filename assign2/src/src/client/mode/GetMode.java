package client.mode;

import requests.store.GetRequest;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class GetMode extends TcpMode {
    String key;
    public GetMode(String nodeAp, String key) {
        super(nodeAp);
        this.key = key;
    }

    @Override
    public void execute() {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(getHost(), getPort());
            OutputStream os = clientSocket.getOutputStream();
            InputStream is = clientSocket.getInputStream();
            GetRequest request = new GetRequest(key);
            request.send(os);
            is.transferTo(System.out);
        } catch (SocketException e) {
            try {
                assert clientSocket != null;
                clientSocket.getInputStream().transferTo(System.out);
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

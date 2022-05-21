package client.mode;

import client.TestClient;
import requests.DeleteRequest;
import requests.GetRequest;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DeleteMode extends TcpMode {
    String key;
    public DeleteMode(String nodeAp, String key) {
        super(nodeAp);
        this.key = key;
    }

    @Override
    public void execute() {
        try (Socket socket = new Socket(getHost(), getPort())) {
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            DeleteRequest request = new DeleteRequest(key);
            request.send(os);
            is.transferTo(System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

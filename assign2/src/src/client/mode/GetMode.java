package client.mode;

import requests.store.GetRequest;

import java.io.*;
import java.net.Socket;

public class GetMode extends TcpMode {
    String key;
    public GetMode(String nodeAp, String key) {
        super(nodeAp);
        this.key = key;
    }

    @Override
    public void execute() {
        try (Socket socket = new Socket(getHost(), getPort())) {
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            GetRequest request = new GetRequest(key);
            request.send(os);
            is.transferTo(System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

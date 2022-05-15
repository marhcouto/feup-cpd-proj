package client.mode;

import requests.GetRequest;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
            os.write(request.toNetworkBytes());
            byte[] res = is.readAllBytes();
            System.out.println(new String(res, StandardCharsets.UTF_8));
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

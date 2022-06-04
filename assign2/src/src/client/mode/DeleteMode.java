package client.mode;

import requests.store.DeleteRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class DeleteMode extends TcpMode {
    String key;
    public DeleteMode(String nodeAp, String key) {
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
            DeleteRequest request = new DeleteRequest(key);
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

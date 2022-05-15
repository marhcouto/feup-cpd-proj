package store.requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DispatchClientRequestTask implements Runnable {
    private final Socket clientSocket;

    public DispatchClientRequestTask(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            InputStream in = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();
            os.write("Response!".getBytes(StandardCharsets.UTF_8));
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error communicating with client");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket");
            }
        }
    }
}

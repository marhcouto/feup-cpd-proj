package client.mode;

import requests.PutRequest;
import utils.InvalidArgumentsException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class PutMode extends TcpMode {
    private Path filePath;

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public PutMode(String nodeAp, String filePath) throws InvalidArgumentsException {
        super(nodeAp);
        this.filePath = Paths.get(filePath);
        if (!Files.isRegularFile(this.filePath)) {
            throw new InvalidArgumentsException("File specified in arguments does not exist");
        }
    }

    @Override
    public void execute() {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            algorithm.reset();
            DigestInputStream digest = new DigestInputStream(
                    new FileInputStream(
                            new File(this.filePath.toString())
                    ),
                    algorithm
            );
            while(digest.read() != -1) {}
            PutRequest putRequest = new PutRequest(bytesToHex(algorithm.digest()), filePath.toString());
            Socket clientSocket = new Socket(getHost(), getPort());
            putRequest.send(clientSocket.getOutputStream());
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

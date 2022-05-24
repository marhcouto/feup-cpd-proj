package client.mode;

import requests.PutRequest;
import utils.InvalidArgumentsException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import static utils.FileKeyCalculate.fileToKey;

public class PutMode extends TcpMode {
    private Path filePath;

    public PutMode(String nodeAp, String filePath) throws InvalidArgumentsException {
        // TODO: filePath to fileName
        super(nodeAp);
        System.out.println(filePath);
        this.filePath = Paths.get(filePath);
        if (!Files.isRegularFile(this.filePath)) {
            throw new InvalidArgumentsException("File specified in arguments does not exist");
        }
    }

    @Override
    public void execute() {
        try {
            System.out.println(fileToKey(new FileInputStream(filePath.toString())));
            PutRequest putRequest = new PutRequest(fileToKey(new FileInputStream(filePath.toString())), filePath.toString());
            Socket clientSocket = new Socket(getHost(), getPort());
            putRequest.send(clientSocket.getOutputStream());
            clientSocket.getInputStream().transferTo(System.out);
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

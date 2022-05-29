package client.mode;

import requests.store.PutRequest;
import utils.InvalidArgumentsException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static utils.algorithms.FileKeyCalculate.fileToKey;

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
            PutRequest putRequest = new PutRequest(fileToKey(new FileInputStream(filePath.toString())), filePath.toString());
            System.out.println("File processed has hash: " + putRequest.getKey());
            Socket clientSocket = new Socket(getHost(), getPort());
            putRequest.send(clientSocket.getOutputStream());
            clientSocket.getInputStream().transferTo(System.out);
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

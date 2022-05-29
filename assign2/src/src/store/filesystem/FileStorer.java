package store.filesystem;

import requests.NetworkSerializable;
import requests.store.PutRequest;
import store.node.NodeState;
import utils.NeighbourhoodAlgorithms;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static utils.FileKeyCalculate.fileToKey;

public class FileStorer extends NodeFileHandler {

    private final String fileFolder;

    public FileStorer(NodeState nodeState) throws IOException {
        super(nodeState);
        this.fileFolder = "store-persistent-storage/" + nodeState.getNodeId() + "/files";
    }

    @Override
    protected void build() throws IOException {
        Path filePath = Paths.get(this.fileFolder);
        if (!Files.exists(filePath)) {
            Files.createDirectory(filePath);
        }
    }

    public List<File> getFiles() throws FileNotFoundException {
        List<File> files = new ArrayList<>();
        File dir = new File(this.fileFolder);
        if (!dir.isDirectory()) {
            throw new FileNotFoundException();
        }
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            files.add(new File(file.getName()));
        }
        return files;
    }

    public void distributeFiles() throws IOException {
        List<File> files = getFiles();
        for (File file : files) {
            // TODO: change algorithm for replication
            String nearestNodeId = new NeighbourhoodAlgorithms(getNodeState()).findHeir(file.getName());
            String filePath = Paths.get(String.format(this.fileFolder + "/%s", file.getName())).toString();
            PutRequest request = new PutRequest(fileToKey(new FileInputStream(filePath)), filePath);
            Socket neighbourNode = new Socket(nearestNodeId, getNodeState().getTcpDataConnectionAddress().getPort());
            request.send(neighbourNode.getOutputStream());
            Files.delete(Paths.get(filePath));
            neighbourNode.close();
        }
    }

    public String saveFile(String key, long fileSize, InputStream fileStream) throws IOException {
        byte[] bodyBytes = new byte[NetworkSerializable.MAX_BODY_CHUNK_SIZE];
        int totalReadFileBytes = 0;
        Path filePath = Paths.get(String.format(this.fileFolder + "/%s", key));
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        try (OutputStream fs = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)) {
            while(totalReadFileBytes < fileSize) {
                int curReadFileBytes = fileStream.read(bodyBytes);
                if (curReadFileBytes == -1) {
                    break;
                }
                totalReadFileBytes += curReadFileBytes;
                fs.write(Arrays.copyOfRange(bodyBytes, 0, curReadFileBytes));
            }
        }
        return filePath.toString();
    }

    public Path getFile(String key) throws FileNotFoundException{
        Path filePath = Paths.get(String.format(this.fileFolder + "/%s", key));
        if (! Files.exists(filePath)) throw new FileNotFoundException();
        return filePath;
    }

}

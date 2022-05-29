package requests;

import store.state.NodeState;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class PutRequest extends NetworkSerializable implements NetworkRequest {
    private final String fileKey;
    private final String filePath;
    private int fileSize;
    private Boolean replicate;

    public PutRequest(PutRequest copy, Boolean replicate) {
        this.replicate = false;
        this.fileKey = copy.getKey();
        this.filePath = copy.getFilePath();
    }

    public PutRequest(String fileKey, String filePath, Boolean replicate) {
        this.fileKey = fileKey;
        this.filePath = filePath;
        this.replicate = replicate;
    }

    public PutRequest(String fileKey, String filePath) {
        this(fileKey, filePath, true);
    }

    @Override
    public String getKey() {
        return fileKey;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getFileSize() {
        return fileSize;
    }

    public boolean needToReplicate() {
        return replicate;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        Path filePathObj = Path.of(filePath);
        String header = RequestType.PUT + endOfLine +
            fileKey + endOfLine +
                Files.size(filePathObj) + endOfLine +
                replicate + endOfLine
                + endOfLine;
        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));
        Files.copy(filePathObj, outputStream);
    }

    public static PutRequest fromNetworkStream(NodeState nodeState, String[] headers, InputStream fileStream) throws IOException {
        String key = headers[1];
        long fileSize = Long.parseLong(headers[2]);
        Boolean replicate = Boolean.parseBoolean(headers[3]);
        String filePath = nodeState.getStoreFiles().saveFiles(key, fileSize, fileStream);
        return new PutRequest(key, filePath, replicate);
    }
}

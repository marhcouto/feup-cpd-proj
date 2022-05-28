package requests;

import store.state.NodeState;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;

public class PutRequest extends NetworkSerializable implements NetworkRequest {
    private final String fileKey;
    private final String filePath;
    private int fileSize;

    public PutRequest(String fileKey, String filePath) {
        this.fileKey = fileKey;
        this.filePath = filePath;
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

    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.PUT + endOfLine +
            fileKey + endOfLine +
                Files.size(Paths.get(filePath)) + endOfLine
                + endOfLine;
        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));
        Files.copy(Paths.get(filePath), outputStream);
    }

    public static PutRequest fromNetworkStream(NodeState nodeState, String[] headers, InputStream fileStream) throws IOException {
        String key = headers[1];
        long fileSize = Long.parseLong(headers[2]);
        String filePath = nodeState.getStoreFiles().saveFiles(key, fileSize, fileStream);
        return new PutRequest(key, filePath);
    }
}

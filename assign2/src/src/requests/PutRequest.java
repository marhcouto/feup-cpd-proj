package requests;

import store.requests.PutRequestHandler;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;

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

    public static PutRequest fromNetworkStream(String nodeId, String[] headers, InputStream fileStream) throws IOException {
        // TODO: refactor - abstract the file saving part to a different function
        byte[] bodyBytes = new byte[NetworkSerializable.MAX_BODY_CHUNK_SIZE];
        int totalReadFileBytes = 0;
        String key = headers[1];
        long fileSize = Long.parseLong(headers[2]);
        Boolean replicate = Boolean.parseBoolean(headers[3]);
        Path filePath = Paths.get(String.format("store-persistent-storage/%s/%s", nodeId, key));
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
        return new PutRequest(key, filePath.toString(), replicate);
    }
}

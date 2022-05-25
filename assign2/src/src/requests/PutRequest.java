package requests;

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

    public static PutRequest fromNetworkStream(String nodeId, String[] headers, InputStream fileStream) throws IOException {
        byte[] bodyBytes = new byte[NetworkSerializable.MAX_BODY_CHUNK_SIZE];
        int totalReadFileBytes = 0;
        String key = headers[1];
        long fileSize = Long.parseLong(headers[2]);
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
        return new PutRequest(key, filePath.toString());
    }
}

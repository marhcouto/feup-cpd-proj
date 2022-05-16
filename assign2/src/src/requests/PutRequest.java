package requests;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PutRequest extends NetworkSerializable{
    private String fileKey;
    private String filePath;
    private int fileSize;

    public PutRequest(String fileKey, String filePath) {
        System.out.println(fileKey);
        System.out.println(filePath);
        this.fileKey = fileKey;
        this.filePath = filePath;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.PUT + endOfLine +
            fileKey + endOfLine +
            Files.size(Paths.get(filePath)) + endOfLine;
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        FileInputStream fs = new FileInputStream(new File(filePath));
        int readBytes;
        byte[] fileBuffer = new byte[4096];
        while((readBytes = fs.read(fileBuffer)) != -1) {
            outputStream.write(fileBuffer, 0, readBytes);
        }
        fs.close();
    }

    public static PutRequest fromNetworkStream(String nodeId, String[] headers, InputStream fileStream) throws IOException {
        byte[] headerBytes = new byte[512];
        String key = headers[1];
        int fileSize = Integer.parseInt(headers[2]);
        Path filePath = Paths.get(String.format("store-persistent-storage/%s/%s", nodeId, key));
        Files.createFile(filePath);
        byte[] readerBuffer = new byte[4096];
        int readBytes;
        FileOutputStream fs = new FileOutputStream(new File(filePath.toString()));
        while((readBytes = fileStream.read(readerBuffer)) != -1) {
            fs.write(readerBuffer, 0, readBytes);
        }
        fs.close();
        return new PutRequest(key, filePath.toString());
    }
}

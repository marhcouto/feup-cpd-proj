package requests;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SeekRequest extends NetworkSerializable implements NetworkRequest {
    public static final String FILE_FOUND_MESSAGE = "EXISTS";
    public static final String FILE_NOT_FOUND_MESSAGE = "NOT FOUND";
    private String fileKey;

    public SeekRequest(String fileKey) {
        this.fileKey = fileKey;
    }

    @Override
    public String getKey() {
        return fileKey;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.SEEK + endOfLine
                + getKey() + endOfLine
                + endOfLine;
        outputStream.write(header.getBytes(StandardCharsets.US_ASCII));
    }

    public static SeekRequest fromNetworkStream(String[] headers) {
        return new SeekRequest(headers[1]);
    }
}

package requests.store;

import requests.NetworkRequest;
import requests.NetworkSerializable;
import requests.RequestType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DeleteRequest extends NetworkSerializable implements NetworkRequest {
    private final String key;
    private final Boolean replicate;

    public DeleteRequest(DeleteRequest copy, Boolean replicate) {
        this.key = copy.getKey();
        this.replicate = replicate;
    }

    public DeleteRequest(String key, Boolean replicate) {
        this.key = key;
        this.replicate = replicate;
    }

    public DeleteRequest(String key) {
        this(key, true);
    }
    @Override
    public String getKey() {
        return key;
    }

    public boolean needToReplicate() {
        return replicate;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException {
        String header = RequestType.DELETE + endOfLine +
                key + endOfLine
                + endOfLine;
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
    }

    public static DeleteRequest fromNetworkStream(String[] headers) {
        return new DeleteRequest(headers[1]);
    }
}

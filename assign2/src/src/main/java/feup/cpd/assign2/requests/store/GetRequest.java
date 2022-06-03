package requests.store;

import requests.NetworkRequest;
import requests.NetworkSerializable;
import requests.RequestType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.charset.StandardCharsets;

public class GetRequest extends NetworkSerializable implements NetworkRequest {
    public static final String ERROR_NOT_FOUND = "ERROR: Key not found\n";
    public static final int ERROR_NOT_FOUND_SIZE = ERROR_NOT_FOUND.getBytes(StandardCharsets.US_ASCII).length;

    private final String key;
    private final Boolean replicate;

    public GetRequest(GetRequest copy, boolean replicate) {
        this.key = copy.getKey();
        this.replicate = replicate;
    }

    public GetRequest(String key, boolean replicate) {
        this.key = key;
        this.replicate = replicate;
    }

    public GetRequest(String key) {
        this(key, true);
    }

    public String getKey() {
        return key;
    }

    public boolean needToReplicate() {
        return replicate;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException{
        String header = RequestType.GET + endOfLine +
            key + endOfLine
            + needToReplicate() + endOfLine
            + endOfLine;
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
    }

    public static GetRequest fromNetworkStream(String[] headers) {
        return new GetRequest(headers[1], Boolean.parseBoolean(headers[2]));
    }
}

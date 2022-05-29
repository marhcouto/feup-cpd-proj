package requests.store;

import requests.NetworkRequest;
import requests.NetworkSerializable;
import requests.RequestType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class GetRequest extends NetworkSerializable implements NetworkRequest {
    private final String key;

    public GetRequest(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public void send(OutputStream outputStream) throws IOException{
        String header = RequestType.GET + endOfLine +
            key + endOfLine
            + endOfLine;
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
    }

    public static GetRequest fromNetworkStream(String[] headers) {
        return new GetRequest(headers[1]);
    }
}

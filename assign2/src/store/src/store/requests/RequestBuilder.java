package store.requests;

import java.nio.charset.StandardCharsets;

public class RequestBuilder {
    StringBuilder message;
    String body;

    public RequestBuilder(RequestType requestType) {
        message.append(requestType + "\r\n");
    }

    public RequestBuilder addHeader(String headerType, String headerValue) {
        message.append(String.format("%s %s\r\n", headerType, headerValue));
        return this;
    }

    public RequestBuilder addBody(String body) {
        this.body = body;
        return this;
    }

    public byte[] toBytes() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return String.format("%s\r\n%s", message, body);
    }
}

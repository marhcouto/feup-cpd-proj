package requests;

import requests.exceptions.MessageOutOfBounds;

public class RequestBuilder {
    StringBuilder header = new StringBuilder();
    byte[] body;

    public RequestBuilder(RequestType requestType) {
        header.append(requestType).append("\r\n");
    }

    public RequestBuilder addHeader(String headerType, String headerValue) {
        header.append(String.format("%s %s\r\n", headerType, headerValue));
        return this;
    }

    public RequestBuilder addBody(byte[] body) {
        this.body = body;
        return this;
    }

    public byte[] toBytes() throws MessageOutOfBounds {
        byte[] headerBytes = header.toString().getBytes();
        int msgSize = body.length + headerBytes.length + 2;
        if (msgSize > RequestConstants.MAX_MESSAGE_SIZE) {
            throw new MessageOutOfBounds("File and headers created a message bigger than expected");
        }
        byte[] messageBytes = new byte[msgSize];
        int curMessageIdx = 0;
        for (byte headerByte : headerBytes) {
            messageBytes[curMessageIdx++] = headerByte;
        }
        messageBytes[curMessageIdx++] = Byte.parseByte("\r");
        messageBytes[curMessageIdx++] = Byte.parseByte("\n");
        for (byte bodyByte: body) {
            messageBytes[curMessageIdx++] = bodyByte;
        }
        return messageBytes;
    }
}

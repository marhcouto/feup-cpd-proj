package requests;

import requests.exceptions.InvalidByteArray;

import java.util.List;

public class GetRequest extends NetworkSerializable<GetRequest>{
    private final String key;

    public GetRequest(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    private static boolean compatibleArray(byte[] networkBytes) {
        byte[] requestType = RequestType.GET.toString().getBytes();
        for (int i = 0; i < requestType.length; i++) {
            if (networkBytes[i] != requestType[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public GetRequest fromNetworkBytes(byte[] networkBytes) throws InvalidByteArray {
        if (!compatibleArray(networkBytes)) {
            throw new InvalidByteArray("Invalid byte array");
        }
        StringBuilder header = new StringBuilder();
        int endOfHeader = findEndOfHeader(networkBytes);
        for (int i = 0; i <= endOfHeader; i++) {
            header.append((char) networkBytes[i]);
        }
        String[] headerElements = header.toString().split(endOfLine);
        if (headerElements.length < 2) {
            throw new InvalidByteArray("The message didn't contained the key of the data");
        }
        return new GetRequest(headerElements[1]);
    }

    @Override
    public byte[] toNetworkBytes() {
        return (RequestType.GET.toString() +
                endOfLine +
                key +
                endOfLine +
                endOfLine)
            .getBytes();
    }
}

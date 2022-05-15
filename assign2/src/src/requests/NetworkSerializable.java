package requests;

import requests.exceptions.InvalidByteArray;

public abstract class NetworkSerializable<T> {
    protected final String endOfLine = "\r\n";

    protected int findEndOfHeader(byte[] byteArr) {
        for (int i = 0; i < byteArr.length - 3; i++) {
            if (byteArr[i] == '\r' && byteArr[i + 1] == '\n' && byteArr[i + 2] == '\r' && byteArr[i + 3] == '\n') {
                return i + 1;
            }
        }
        return -1;
    }

    public abstract byte[] toNetworkBytes();

    public abstract T fromNetworkBytes(byte[] networkBytes) throws InvalidByteArray;
}

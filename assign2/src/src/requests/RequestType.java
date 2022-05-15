package requests;

import requests.exceptions.InvalidByteArray;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class RequestType {
    public static final String MEMBERSHIP = "MEMBERSHIP";
    public static final String JOIN = "JOIN";
    public static final String GET = "GET";

    public String determineRequestType(byte[] dataArr) throws InvalidByteArray {
        int endOfFirstLine = -1;
        for (int i = 0; i < dataArr.length - 1; i++) {
            if (dataArr[i] == '\r' && dataArr[i + 1] == '\n') {
                endOfFirstLine = i - 1;
            }
        }
        if (endOfFirstLine == -1) {
            throw new InvalidByteArray("ERROR: Request has no type");
        }
        return new String(Arrays.copyOfRange(dataArr, 0, endOfFirstLine), StandardCharsets.UTF_8);
    }
}

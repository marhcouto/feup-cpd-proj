package requests;

import requests.exceptions.InvalidByteArray;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class RequestType {
    public static final String MEMBERSHIP = "MEMBERSHIP";
    public static final String JOIN = "JOIN";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
}

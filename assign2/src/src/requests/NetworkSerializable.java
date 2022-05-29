package requests;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class NetworkSerializable {

    public static final int MAX_HEADER_SIZE = 512;
    public static final int MAX_BODY_CHUNK_SIZE = 4096;
    protected static final String endOfLine = "\r\n";

    public static String[] getHeader(InputStream is) throws IOException {
        byte[] header = new byte[MAX_HEADER_SIZE];
        for (int i = 0; i < MAX_HEADER_SIZE; i++) {
            int curByte = is.read();
            if (curByte == -1) {
                break;
            }
            header[i] = (byte) curByte;
            if (i >= 3 && header[i] == '\n' && header[i - 1] == '\r' && header[i - 2] == '\n' && header[i - 3] == '\r') {
                return new String(Arrays.copyOfRange(header, 0, i - 3), StandardCharsets.US_ASCII).split("\r\n");
            }
        }
        return null;
    }

    public abstract void send(OutputStream outputStream) throws IOException;
}

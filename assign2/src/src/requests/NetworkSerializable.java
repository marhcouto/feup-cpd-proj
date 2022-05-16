package requests;

import requests.exceptions.InvalidByteArray;

import java.io.*;

public abstract class NetworkSerializable {

    private static final int MAX_TEXT_LINE_SIZE = 256;
    protected final String endOfLine = "\r\n";

    public static String readLine(InputStream is) throws IOException {
        char[] lineBuffer = new char[MAX_TEXT_LINE_SIZE];
        InputStreamReader inputStreamReader = new InputStreamReader(is);
        int curLineBufferIndex = 0;
        char lastChar = '\0';
        while (true) {
            int charInt = inputStreamReader.read();
            if (charInt == -1) {
                return null;
            }
            char curChar = (char) charInt;
            if (curChar == '\n' && lastChar == '\r') {
                return new String(lineBuffer);
            } else if (curLineBufferIndex < MAX_TEXT_LINE_SIZE) {
                lastChar = curChar;
                lineBuffer[curLineBufferIndex++] = curChar;
            }
        }
    }

    public abstract void send(OutputStream outputStream) throws IOException;
}

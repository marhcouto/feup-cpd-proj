package store.requests;

import java.io.*;

public interface RequestHandler {
    void execute(String[] headers) throws IOException;
}

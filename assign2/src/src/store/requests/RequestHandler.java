package store.requests;

import java.io.*;

public interface RequestHandler {
    void execute(InputStream messageStream) throws IOException;
}

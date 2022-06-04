package store.handlers.store;

import requests.store.SeekRequest;
import store.node.NodeState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SeekRequestHandler extends StoreRequestHandler {
    public SeekRequestHandler(NodeState state) {
        super(state);
    }

    @Override
    public void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        SeekRequest request = SeekRequest.fromNetworkStream(headers);
        System.out.println("Received" + " SEEK request");
        try {
            getNodeState().getFileStorer().getFilePath(request.getKey());
            responseStream.write(SeekRequest.FILE_FOUND_MESSAGE.getBytes(StandardCharsets.US_ASCII));
        } catch (FileNotFoundException e) {
            responseStream.write(SeekRequest.FILE_NOT_FOUND_MESSAGE.getBytes(StandardCharsets.US_ASCII));
        }
    }
}

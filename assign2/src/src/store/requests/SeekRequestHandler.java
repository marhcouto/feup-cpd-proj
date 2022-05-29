package store.requests;

import requests.SeekRequest;
import store.state.NodeState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SeekRequestHandler extends RequestHandler {
    public SeekRequestHandler(NodeState state) {
        super(state);
    }

    @Override
    void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        SeekRequest request = SeekRequest.fromNetworkStream(headers);
        if (Files.exists(Paths.get(String.format("store-persistent-storage/%s/%s", getNodeState().getNodeId(), request.getKey())))) {
            responseStream.write(SeekRequest.FILE_FOUND_MESSAGE.getBytes(StandardCharsets.US_ASCII));
        } else {
            responseStream.write(SeekRequest.FILE_NOT_FOUND_MESSAGE.getBytes(StandardCharsets.US_ASCII));
        }
    }
}

package store.requests;

import requests.PutRequest;
import store.NodeState;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PutRequestHandler implements RequestHandler {
    private NodeState state;
    private InputStream fileStream;

    public PutRequestHandler(NodeState state, InputStream fileStream) {
        this.state = state;
        this.fileStream = fileStream;
    }

    @Override
    public void execute(String[] headers) throws IOException {
        PutRequest request = PutRequest.fromNetworkStream(state.getNodeId(), headers, fileStream);
    }
}

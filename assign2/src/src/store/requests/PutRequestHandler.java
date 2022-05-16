package store.requests;

import requests.PutRequest;
import store.NodeState;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PutRequestHandler implements RequestHandler {
    private NodeState state;

    public PutRequestHandler(NodeState state) {
        this.state = state;
    }

    @Override
    public void execute(InputStream messageStream) throws IOException {
        PutRequest request = PutRequest.fromNetworkStream(state.getNodeId(), messageStream);
    }
}

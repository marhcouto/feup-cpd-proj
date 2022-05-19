package store.requests;

import requests.PutRequest;
import store.state.NodeState;
import store.membership.filesystem.Neighbour;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PutRequestHandler implements RequestHandler {
    private final InputStream fileStream;
    private final OutputStream responseStream;
    private final NodeState state;

    public PutRequestHandler(NodeState state, OutputStream responseStream, InputStream fileStream) {
        this.state = state;
        this.fileStream = fileStream;
        this.responseStream = responseStream;
    }

    @Override
    public void execute(String[] headers) throws IOException {
        PutRequest request = PutRequest.fromNetworkStream(state.getNodeId(), headers, fileStream);
        try {
            String neighbourId = state.findNearestNeighbour(request);
            if (neighbourId.equals(state.getNodeId())) {
                responseStream.write("Success: File was stored".getBytes(StandardCharsets.UTF_8));
            } else {
                Socket neighbourNode = new Socket(neighbourId, 3000);
                request.send(neighbourNode.getOutputStream());
                //Pipes response into client socket
                neighbourNode.getInputStream().transferTo(responseStream);
                Files.delete(Paths.get(request.getFilePath()));
                neighbourNode.close();
            }
        } catch (NoSuchAlgorithmException e) {
            //This shouldn't happen
            e.printStackTrace();
        }
    }
}

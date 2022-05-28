package store.requests;

import requests.PutRequest;
import store.state.NodeState;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PutRequestHandler extends RequestHandler {
    public PutRequestHandler(NodeState state) {
        super(state);
    }

    @Override
    public void execute(String[] headers, OutputStream responseStream, InputStream clientStream) throws IOException {
        // TODO: change order of events to avoid saving and deletion
        PutRequest request = PutRequest.fromNetworkStream(getNodeState(), headers, clientStream);
        String neighbourId = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());
        if (neighbourId.equals(getNodeState().getNodeId())) {
            responseStream.write("Success: File was stored\n".getBytes(StandardCharsets.UTF_8));
        } else {
            Socket neighbourNode = new Socket(neighbourId, getNodeState().getTcpDataConnectionAddress().getPort());
            request.send(neighbourNode.getOutputStream());
            //Pipes response into client socket
            neighbourNode.getInputStream().transferTo(responseStream);
            Files.delete(Paths.get(request.getFilePath()));
            neighbourNode.close();
        }
    }
}

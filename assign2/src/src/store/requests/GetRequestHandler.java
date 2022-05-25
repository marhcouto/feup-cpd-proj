package store.requests;

import requests.GetRequest;
import store.state.NodeState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static utils.NearestNeighbour.findNearestNeighbour;

public class GetRequestHandler extends RequestHandler {
    public GetRequestHandler(NodeState nodeState) {
        super(nodeState);
    }

    @Override
    void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        GetRequest request = GetRequest.fromNetworkStream(headers);
        Path filePath = Paths.get(String.format("store-persistent-storage/%s/%s", getNodeState().getNodeId(), request.getKey()));
        String neighbourId = findNearestNeighbour(request.getKey());
        if (neighbourId.equals(getNodeState().getNodeId())) {
            if (!Files.exists(filePath)) {
                responseStream.write("ERROR: Key not found\n".getBytes(StandardCharsets.UTF_8));
            } else {
                Files.copy(filePath, responseStream);
            }
        } else {
            Socket neighbourSocket = new Socket(neighbourId, 3000);
            request.send(neighbourSocket.getOutputStream());
            neighbourSocket.getInputStream().transferTo(responseStream);
            neighbourSocket.close();
        }

    }
}

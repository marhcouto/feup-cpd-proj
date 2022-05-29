package store.requests;

import requests.GetRequest;
import requests.SeekRequest;
import store.state.NodeState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GetRequestHandler extends RequestHandler {
    public GetRequestHandler(NodeState nodeState) {
        super(nodeState);
    }

    @Override
    void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        GetRequest request = GetRequest.fromNetworkStream(headers);
        Path filePath = Paths.get(String.format("store-persistent-storage/%s/%s", getNodeState().getNodeId(), request.getKey()));
        List<String> allDest = getNeighbourhoodAlgorithms().findReplicationNodes(request.getKey());
        if (allDest.contains(getNodeState().getNodeId()) && Files.exists(filePath)) {
            Files.copy(filePath, responseStream);
            return;
        }
        // Removes the actual node because we already checked if the file existed
        allDest.removeIf(nodeId -> nodeId.equals(getNodeState().getNodeId()));
        SeekRequest seekRequest = new SeekRequest(request.getKey());
        for (var nodeId: allDest) {
            try {
                Socket neighbourSocket = new Socket(nodeId, 3000);
                OutputStream neighbourOutputStream = neighbourSocket.getOutputStream();
                InputStream neighbourInputStream = neighbourSocket.getInputStream();
                seekRequest.send(neighbourOutputStream);
                String seekResponse = new String(neighbourInputStream.readAllBytes(), StandardCharsets.US_ASCII);
                neighbourSocket.close();
                if (seekResponse.equals(SeekRequest.FILE_FOUND_MESSAGE)) {
                    neighbourSocket = new Socket(nodeId, 3000);
                    request.send(neighbourSocket.getOutputStream());
                    neighbourSocket.getInputStream().transferTo(responseStream);
                    neighbourSocket.close();
                    break;
                }
            } catch (IOException e) {
                break;
            }
        }
    }
}

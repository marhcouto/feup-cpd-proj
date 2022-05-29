package store.handlers.store;

import requests.store.GetRequest;
import requests.store.SeekRequest;
import store.node.NodeState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GetRequestHandler extends StoreRequestHandler {
    public GetRequestHandler(NodeState nodeState) {
        super(nodeState);
    }

    @Override
    public void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        // TODO: sends connection refused in else
        GetRequest request = GetRequest.fromNetworkStream(headers);
        List<String> allDest = getNeighbourhoodAlgorithms().findReplicationNodes(request.getKey());
        if (allDest.contains(getNodeState().getNodeId())) {
            try {
                Path filePath = getNodeState().getStoreFiles().getFile(request.getKey());
                Files.copy(filePath, responseStream);
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                responseStream.write("ERROR: Key not found\n".getBytes(StandardCharsets.UTF_8));
            }
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

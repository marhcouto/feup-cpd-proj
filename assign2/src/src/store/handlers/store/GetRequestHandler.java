package store.handlers.store;

import requests.store.GetRequest;
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
        GetRequest request = GetRequest.fromNetworkStream(headers);
        System.out.println("Received GET request of file with key " + headers[1]);
        List<String> allDest = getNeighbourhoodAlgorithms().findReplicationNodes(request.getKey());
        if (allDest.contains(getNodeState().getNodeId())) {
            try {
                Path filePath = getNodeState().getFileStorer().getFilePath(request.getKey());
                Files.copy(filePath, responseStream);
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                responseStream.write(GetRequest.ERROR_NOT_FOUND.getBytes(StandardCharsets.UTF_8));
            }
        }
        if (!request.needToReplicate()) {
            return;
        }
        // Removes the actual node because we already checked if the file existed
        GetRequest nonReplicateRequest = new GetRequest(request, false);
        allDest.removeIf(nodeId -> nodeId.equals(getNodeState().getNodeId()));
        for (var nodeId: allDest) {
            try {
                Socket neighbourSocket = new Socket(nodeId, 3000);
                nonReplicateRequest.send(neighbourSocket.getOutputStream());
                InputStream is = neighbourSocket.getInputStream();
                byte[] responseInit = is.readNBytes(GetRequest.ERROR_NOT_FOUND_SIZE);
                if (responseInit.length != GetRequest.ERROR_NOT_FOUND_SIZE || !new String(responseInit, StandardCharsets.US_ASCII).equals(GetRequest.ERROR_NOT_FOUND)) {
                    responseStream.write(responseInit);
                    is.transferTo(responseStream);
                    return;
                }
                neighbourSocket.close();
            } catch (IOException e) {
                break;
            }
        }
        responseStream.write(GetRequest.ERROR_NOT_FOUND.getBytes(StandardCharsets.US_ASCII));
    }
}

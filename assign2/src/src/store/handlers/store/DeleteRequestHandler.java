package store.handlers.store;

import requests.store.DeleteRequest;
import store.node.NodeState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DeleteRequestHandler extends StoreRequestHandler {
    private static final String SUCCESS_MESSAGE = "SUCCESS: File deleted";
    private static final String ERROR_DELETING_FILE = "ERROR: Couldn't delete file";

    public DeleteRequestHandler(NodeState state) {
        super(state);
    }

    private void replicateRequest(DeleteRequest request) {
        List<String> allDest = getNeighbourhoodAlgorithms().findReplicationNodes(request.getKey());
        allDest.removeIf(elem -> getNodeState().getNodeId().equals(elem));
        DeleteRequest nonReplicateRequest = new DeleteRequest(request, false);
        for (var nodeId: allDest) {
            try(Socket neighSocket = new Socket(nodeId, getNodeState().getTcpDataConnectionAddress().getPort())) {
                nonReplicateRequest.send(neighSocket.getOutputStream());
            } catch (IOException e) {
                System.out.printf("Couldn't connect to node '%s'%n", nodeId);
            }
        }
    }

    @Override
    public void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        DeleteRequest request = DeleteRequest.fromNetworkStream(headers);
        String requestDest = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());
        if (!request.needToReplicate()) {
            try {
                List<String> allRepCandidates = getNeighbourhoodAlgorithms().findReplicationNodes(request.getKey());
                if (allRepCandidates.contains(getNodeState().getNodeId())) {
                    Path filePath = getNodeState().getStoreFiles().getFilePath(request.getKey());
                    Files.delete(filePath);
                    Files.createFile(Paths.get(filePath + "_DEL"));
                    Files.writeString(filePath, Long.valueOf(System.currentTimeMillis()).toString(), StandardCharsets.US_ASCII);
                }
            } catch (FileNotFoundException e) { /* Nothing to do */}
            responseStream.write(SUCCESS_MESSAGE.getBytes(StandardCharsets.US_ASCII));
            return;
        }
        if (requestDest.equals(getNodeState().getNodeId())) {
            replicateRequest(request);
            try {
                Files.delete(getNodeState().getStoreFiles().getFilePath(request.getKey()));
            } catch (FileNotFoundException e) { /* Nothing to do */}
            replicateRequest(request);
            return;
        }
        // Handles communication errors with other servers
        // Communication errors between server and client should be handled elsewhere
        try(Socket rightDest = new Socket(requestDest, getNodeState().getTcpDataConnectionAddress().getPort())) {
            request.send(rightDest.getOutputStream());
            rightDest.getInputStream().transferTo(responseStream);
        } catch (IOException e) {
            responseStream.write(ERROR_DELETING_FILE.getBytes(StandardCharsets.US_ASCII));
        }
    }
}

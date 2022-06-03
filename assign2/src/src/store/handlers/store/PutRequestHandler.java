package store.handlers.store;

import requests.store.PutRequest;
import store.node.NodeState;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PutRequestHandler extends StoreRequestHandler {
    private static final String SUCCESS_MESSAGE = "SUCCESS: File stored";
    private static final String ERROR_SENDING_FILE = "ERROR: Couldn't send the file";
    private static final String ERROR_WRONG_DEST = "ERROR: This node doesn't handle this key";
    private static final String ERROR_REJ_FILE = "ERROR: This node doesn't handle this key";

    public PutRequestHandler(NodeState state) {
        super(state);
    }

    private void replicateRequest(PutRequest request) {
        List<String> allDest = getNeighbourhoodAlgorithms().findReplicationNodes(request.getKey());
        allDest.removeIf(elem -> getNodeState().getNodeId().equals(elem));
        PutRequest nonReplicateRequest = new PutRequest(request, false);
        for (var nodeId: allDest) {
            try(Socket neighSocket = new Socket(nodeId, getNodeState().getTcpDataConnectionAddress().getPort())) {
                nonReplicateRequest.send(neighSocket.getOutputStream());
                String response = new String(neighSocket.getInputStream().readAllBytes(), StandardCharsets.US_ASCII);
                if (!response.equals(SUCCESS_MESSAGE)) {
                    System.out.printf("Node '%s' rejected replica of '%s'%n", nodeId, request.getKey());
                }
            } catch (IOException e) {
                System.out.printf("Couldn't connect to node '%s'%n", nodeId);
            }
        }
    }

    @Override
    public void execute(String[] headers, OutputStream responseStream, InputStream clientStream) throws IOException {
        PutRequest request = PutRequest.fromNetworkStream(getNodeState(), headers, clientStream);
        String requestDest = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());
        if (getNodeState().getStoreFiles().hasTombstone(request.getKey())) {
            responseStream.write(ERROR_REJ_FILE.getBytes(StandardCharsets.US_ASCII));
        }
        if (!request.needToReplicate()) {
            List<String> allRepCandidates = getNeighbourhoodAlgorithms().findReplicationNodes(request.getKey());
            if (!allRepCandidates.contains(getNodeState().getNodeId())) {
                Files.delete(Paths.get(request.getFilePath()));
                responseStream.write(ERROR_WRONG_DEST.getBytes(StandardCharsets.US_ASCII));
            } else {
                responseStream.write(SUCCESS_MESSAGE.getBytes(StandardCharsets.US_ASCII));
            }
            return;
        }
        if (requestDest.equals(getNodeState().getNodeId())) {
            replicateRequest(request);
            responseStream.write(SUCCESS_MESSAGE.getBytes(StandardCharsets.US_ASCII));
            return;
        }
        // Handles communication errors with other servers
        // Communication errors between server and client should be handled elsewhere
        for (int i = 0; i < NUM_RETRIES; i++) {
            requestDest = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());
            try(Socket rightDest = new Socket(requestDest, getNodeState().getTcpDataConnectionAddress().getPort())) {
                request.send(rightDest.getOutputStream());
                rightDest.getInputStream().transferTo(responseStream);
                Files.delete(Paths.get(request.getFilePath()));
            } catch (IOException e) {
                //TODO: Trigger membership changes
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    return;
                }
            }
        }
    }
}

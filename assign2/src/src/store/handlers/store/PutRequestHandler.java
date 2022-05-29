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
    public PutRequestHandler(NodeState state) {
        super(state);
    }

    @Override
    public void execute(String[] headers, OutputStream responseStream, InputStream clientStream) throws IOException {
        PutRequest request = PutRequest.fromNetworkStream(getNodeState(), headers, clientStream);
        List<String> allDest = getNeighbourhoodAlgorithms().findReplicationNodes(request.getKey());
        System.out.println(String.format("Got PUT request with ID '%s' going to replicate to: %s", request.getKey(), allDest));
        boolean thisNodeIsDest = allDest.contains(getNodeState().getNodeId());
        if (Files.exists(Paths.get(request.getFilePath() + "_DEL"))) {
            Files.delete(Paths.get(request.getFilePath()));
            // Tombstoned
            return;
        }
        if (!request.needToReplicate() && thisNodeIsDest) {
            return;
        }
        if (!request.needToReplicate() && !thisNodeIsDest) {
            Files.delete(Paths.get(request.getFilePath()));
            return;
        }
        for (var nodeId: allDest) {
            if (nodeId.equals(getNodeState().getNodeId())) {
                continue;
            }
            Socket neighbourNode = new Socket(nodeId, getNodeState().getTcpDataConnectionAddress().getPort());
            new PutRequest(request, false).send(neighbourNode.getOutputStream());
            //Pipes response into client socket
            neighbourNode.getInputStream().transferTo(responseStream);
            neighbourNode.close();
        }
        if (!thisNodeIsDest) {
            Files.delete(Paths.get(request.getFilePath()));
        }
        responseStream.write("Success: File storing processed\n".getBytes(StandardCharsets.UTF_8));
    }
}

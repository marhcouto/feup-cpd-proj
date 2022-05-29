package store.requests;

import requests.DeleteRequest;
import store.state.NodeState;

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

public class DeleteRequestHandler extends RequestHandler {
    public DeleteRequestHandler(NodeState state) {
        super(state);
    }

    @Override
    void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        // TODO: sends connection refused in else
        DeleteRequest request = DeleteRequest.fromNetworkStream(headers);
        List<String> allNeigh = getNeighbourhoodAlgorithms().findReplicationNodes(request.getKey());
        if (allNeigh.contains(getNodeState().getNodeId())) {
            try {
                Path filePath = getNodeState().getStoreFiles().getFile(request.getKey());
                Files.delete(filePath);
                Files.createFile(Paths.get(filePath + "_DEL"));
                allNeigh.removeIf(elem -> getNodeState().getNodeId().equals(elem));
                // TODO: implement good feedback messages
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                responseStream.write("ERROR: Key not found\n".getBytes(StandardCharsets.UTF_8));
            }
        } else if (request.needToReplicate()) {
            for (var neighId: allNeigh) {
                DeleteRequest newRequest = new DeleteRequest(request, false);
                Socket neighbourSocket = new Socket(neighId, 3000);
                newRequest.send(neighbourSocket.getOutputStream());
                neighbourSocket.getInputStream().transferTo(responseStream);
                neighbourSocket.close();
            }
        }
        responseStream.write("SUCCESS: Delete processed successfully".getBytes(StandardCharsets.UTF_8));
    }
}

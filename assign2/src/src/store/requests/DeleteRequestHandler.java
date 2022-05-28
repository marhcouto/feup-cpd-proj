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

public class DeleteRequestHandler extends RequestHandler {
    public DeleteRequestHandler(NodeState state) {
        super(state);
    }

    @Override
    void execute(String[] headers, OutputStream responseStream, InputStream clientData) throws IOException {
        DeleteRequest request = DeleteRequest.fromNetworkStream(headers);
        String neighbourId = getNeighbourhoodAlgorithms().findRequestDest(request.getKey());
        if (neighbourId.equals(getNodeState().getNodeId())) {
            try {
                Path filePath = getNodeState().getStoreFiles().getFile(request.getKey());
                Files.delete(filePath);
                responseStream.write("SUCCESS: File was found and deleted".getBytes(StandardCharsets.UTF_8));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                responseStream.write("ERROR: Key not found\n".getBytes(StandardCharsets.UTF_8));
            }
        } else {
            Socket neighbourSocket = new Socket(neighbourId, 3000);
            request.send(neighbourSocket.getOutputStream());
            neighbourSocket.getInputStream().transferTo(responseStream);
            neighbourSocket.close();
        }
    }
}

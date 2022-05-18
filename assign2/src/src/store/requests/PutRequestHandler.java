package store.requests;

import requests.PutRequest;
import store.NodeState;
import store.membership.filesystem.Neighbour;

import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class PutRequestHandler implements RequestHandler {
    private final InputStream fileStream;
    private final OutputStream responseStream;
    private final NodeState state;

    public PutRequestHandler(NodeState state, OutputStream responseStream, InputStream fileStream) {
        this.state = state;
        this.fileStream = fileStream;
        this.responseStream = responseStream;
    }

    @Override
    public void execute(String[] headers) throws IOException {
        PutRequest request = PutRequest.fromNetworkStream(state.getNodeId(), headers, fileStream);
        try {
            Neighbour nearestNeighbour = state.findNearestNeighbour(request);
            if (nearestNeighbour.getNodeId().equals(state.getNodeId())) {
                responseStream.write("Success: File was stored".getBytes(StandardCharsets.UTF_8));
            } else {
                Socket neighbourNode = new Socket();
                neighbourNode.bind(new InetSocketAddress(nearestNeighbour.getNodeId(), 3000));
                request.send(neighbourNode.getOutputStream());
                //Pipes response into client socket
                byte[] humanReadableResponse = new byte[1024];
                int readBytes;
                while ((readBytes = neighbourNode.getInputStream().read(humanReadableResponse)) != -1) {
                    responseStream.write(Arrays.copyOfRange(humanReadableResponse, 0, readBytes));
                }
                neighbourNode.close();
            }
        } catch (NoSuchAlgorithmException e) {
            //This shouldn't happen
            e.printStackTrace();
        }
    }
}

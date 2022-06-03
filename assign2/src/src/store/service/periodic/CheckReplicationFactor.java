package store.service.periodic;

import requests.store.PutRequest;
import requests.store.SeekRequest;
import store.node.NodeState;
import utils.algorithms.NeighbourhoodAlgorithms;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CheckReplicationFactor extends PeriodicActor {
    NeighbourhoodAlgorithms neighAlgs;

    public CheckReplicationFactor(NodeState state) {
        super(state);
        neighAlgs = new NeighbourhoodAlgorithms(state);
    }

    @Override
    protected long getInterval() {
        return 15;
    }

    private void checkReplication(String nodeId, List<String> allKeys) throws IOException {
        for (var key: allKeys) {
            Socket socket = new Socket(nodeId, nodeState.getTcpDataConnectionAddress().getPort());
            SeekRequest seekRequest = new SeekRequest(key);
            seekRequest.send(socket.getOutputStream());
            boolean hasFile = new String(socket.getInputStream().readAllBytes(), StandardCharsets.US_ASCII).equals(SeekRequest.FILE_FOUND_MESSAGE);
            socket.close();
            if (!hasFile) {
                socket = new Socket(nodeId, nodeState.getTcpDataConnectionAddress().getPort());
                PutRequest putRequest = new PutRequest(key, nodeState.getStoreFiles().getFilePath(key).toString(), false);
                putRequest.send(socket.getOutputStream());
                socket.close();
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Check replication factor");
        try {
            List<String> allKeys = new ArrayList<>(nodeState.getStoreFiles().getAllKeys());
            allKeys.removeIf(elem -> elem.equals("files"));
            if (allKeys.isEmpty()) {
                return;
            }
            //All files must have the same replication nodes
            List<String> replicationNodes = neighAlgs.findReplicationNodes(allKeys.get(0));
            for (var nodeId: replicationNodes) {
                try {
                    checkReplication(nodeId, allKeys);
                } catch (IOException e) {
                    //TODO: Maybe multicast membership to make other nodes aware that nodeId is down
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

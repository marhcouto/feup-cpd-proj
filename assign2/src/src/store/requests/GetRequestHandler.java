package store.requests;

import requests.GetRequest;
import store.NodeState;

import java.io.IOException;
import java.io.InputStream;

public class GetRequestHandler implements RequestHandler {

    @Override
    public void execute(String[] headers) throws IOException {
        GetRequest request = GetRequest.fromNetworkStream(headers);
    }
}

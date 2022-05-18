package store.requests;

import requests.GetRequest;

import java.io.IOException;

public class GetRequestHandler implements RequestHandler {

    @Override
    public void execute(String[] headers) throws IOException {
        GetRequest request = GetRequest.fromNetworkStream(headers);
    }
}

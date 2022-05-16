package store.requests;

import requests.GetRequest;

import java.io.IOException;
import java.io.InputStream;

public class GetRequestHandler implements RequestHandler {
    @Override
    public void execute(InputStream messageStream) throws IOException {
        GetRequest request = GetRequest.fromNetworkStream(messageStream);
    }
}

package client.mode;

public abstract class TcpMode implements Mode {
    private final String host;
    private final int port;

    public TcpMode(String nodeAp) {
        String[] apComponent = nodeAp.split(":");
        host = apComponent[0];
        port = Integer.parseInt(apComponent[1]);
    }

    protected String getHost() {
        return host;
    }

    protected int getPort() {
        return port;
    }
}

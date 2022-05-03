package store.membership.filesystem;

public class Log implements FileStorable {
    private String nodeIp;
    private String membershipCounter;

    public Log(String nodeIp, String membershipCounter) {
        this.nodeIp = nodeIp;
        this.membershipCounter = membershipCounter;
    }

    @Override
    public String toFile() {
        return String.format("%s,%s", nodeIp, membershipCounter);
    }
}

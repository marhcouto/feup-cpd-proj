package utils;

public class RmiUtils {

    private final String rmiNodeIdentifier;

    private final String nodeId;

    protected RmiUtils(String rmiNodeIdentifier, String nodeId){
        this.rmiNodeIdentifier = rmiNodeIdentifier;
        this.nodeId = nodeId;
    }

    public int getNodeIdLastDigit(){
        var ipSplit = nodeId.split("\\.");
        var getIpLastId = ipSplit[ipSplit.length - 1];
        try{
            int id = Integer.parseInt(getIpLastId);
            return id;
        }
        catch (NumberFormatException exception){
            exception.printStackTrace();
            return -1;
        }
    }

    public String getRmiNodeIdentifier(){
        return this.rmiNodeIdentifier;
    }

    public String getHost(){
        return this.nodeId;
    }

}

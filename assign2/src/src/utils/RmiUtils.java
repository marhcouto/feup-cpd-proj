package utils;

public class RmiUtils {

    private final String rmiNodeIdentifier;

    private final String nodeId;

    private final int defaultPort;

    protected RmiUtils(String rmiNodeIdentifier, String nodeId){
        this.rmiNodeIdentifier = rmiNodeIdentifier;
        this.nodeId = nodeId;
        this.defaultPort = 1099;
    }

    /**
        Get node access point (127.0.0.2) convert each octet into integer and use it to increment the default port, this way
        collisions are unlikely to happen when doing multiple stores
     **/
    public int getNodeIdLastDigit(){
        var rmiPort = this.defaultPort;
        var ipSplit = nodeId.split("\\.");
        for(String number : ipSplit){
            try{
                int id = Integer.parseInt(number);
                rmiPort += id;
            }
            catch (NumberFormatException exception){
                exception.printStackTrace();
                return -1;
            }
        }
        return rmiPort;
    }

    public String getRmiNodeIdentifier(){
        return this.rmiNodeIdentifier;
    }

    public String getHost(){
        return this.nodeId;
    }

}

package store.membership;

import store.coms.NetworkSerializable;
import store.membership.filesystem.Log;

import java.util.List;

public class MembershipMessage implements NetworkSerializable {
    List<Log> logs;
    List<NodeData> nodeData;

    private static String listToNetworkString(String kindOfSerializable, List<? extends NetworkSerializable> networkSerializables) {
        StringBuilder networkString = new StringBuilder();
        networkString.append(String.format("START %s%s",  kindOfSerializable, endOfLine));
        networkSerializables.stream().forEach(elem -> networkString.append(elem.toNetworkString()));
        networkString.append("END" + endOfLine);
        return networkString.toString();
    }

    @Override
    public String toNetworkString() {
        return listToNetworkString("LOG", logs) + listToNetworkString("NODE_DATA", nodeData);
    }
}

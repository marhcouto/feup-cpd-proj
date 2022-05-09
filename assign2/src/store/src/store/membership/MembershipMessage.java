package store.membership;

import store.coms.NetworkSerializable;
import store.membership.filesystem.Log;

import java.util.List;

public class MembershipMessage implements NetworkSerializable {
    List<Log> logs;
    List<NodeData> nodeData;

    @Override
    public String toNetworkString() {
        return null;
    }
}

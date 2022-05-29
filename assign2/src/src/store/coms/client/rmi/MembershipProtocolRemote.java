package store.coms.client.rmi;

import rmi.MembershipCommands;
import store.State;
import store.state.NodeState;

import java.rmi.RemoteException;

public class MembershipProtocolRemote implements MembershipCommands {

    private final NodeState nodeState;

    public MembershipProtocolRemote(NodeState nodeState){
        this.nodeState = nodeState;
    }

    public NodeState getNodeState(){
        return this.nodeState;
    }

    /*If node waiting for client, means that the node has been created but waiting for the join command*/
    public synchronized Boolean nodeAlreadyJoining(){
        return !this.nodeState.getNodeState().equals(State.WAITING_FOR_CLIENT);
    }

    @Override
    public String join() throws RemoteException {

        if(nodeAlreadyJoining()){
            return "Node with id '" + this.nodeState.getNodeId() + "' has already joined the cluster";
        }

        nodeState.changeNodeState(State.JOINING);

        return "Node Id: '" + nodeState.getNodeId() + "' Membership Protocol for multicast join RMI";
    }

    @Override
    public String leave() throws RemoteException {
        return "Membership Protocol for multicast leave RMI";
    }
}

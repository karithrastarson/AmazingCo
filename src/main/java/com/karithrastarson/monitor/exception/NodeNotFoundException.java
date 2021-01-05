package com.karithrastarson.monitor.exception;

public class NodeNotFoundException extends Exception {

    private long nodeId;

    public NodeNotFoundException(long id) {
        super("Node with id: " + id + " not found");
        this.nodeId = id;
    }

    public long getNodeId() {
        return nodeId;
    }
}

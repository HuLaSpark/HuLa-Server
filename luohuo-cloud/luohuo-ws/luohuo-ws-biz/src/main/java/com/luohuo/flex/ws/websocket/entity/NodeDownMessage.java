package com.luohuo.flex.ws.websocket.entity;

import com.luohuo.basic.mq.redis.core.pubsub.AbstractRedisChannelMessage;

/**
 * 节点下线消息
 */
public class NodeDownMessage extends AbstractRedisChannelMessage {
    private String nodeId;

    public NodeDownMessage() {
    }

    public NodeDownMessage(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getChannel() {
        return "ws-node-down";
    }
}
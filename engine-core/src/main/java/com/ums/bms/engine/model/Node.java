package com.ums.bms.engine.model;

import java.util.List;
import java.util.Map;

import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * 节点定义
 */
@Data
public class Node {
    private String id;
    private String name;
    private NodeType type;
    private String processor;
    private Map<String, Object> parameters;
    private List<String> deps; // 依赖关系
    private NodeStatus status;
    public enum NodeStatus {
        PENDING, RUNNING, COMPLETED, FAILED, SKIPPED
    }
    /**
     * 获取参数值
     * 
     * @param key 参数名
     * @return 参数值
     */
    public @Nullable Object getParameter(String key) {
        return parameters != null ? parameters.get(key) : null;
    }

    public boolean isReady(Map<String, Node> allNodes) {
        if (getDeps().isEmpty()) {
            return true;
        }

        for (String depId : getDeps()) {
            Node depNode = allNodes.get(depId);
            if (depNode == null || depNode.getStatus() != NodeStatus.COMPLETED) {
                return false;
            }
        }
        return true;
    }
}

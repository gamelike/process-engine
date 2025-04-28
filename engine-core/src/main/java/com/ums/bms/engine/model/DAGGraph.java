package com.ums.bms.engine.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ums.bms.engine.utils.deser.NodeMapDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DAG图定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DAGGraph {
    @JsonDeserialize(using = NodeMapDeserializer.class)
    private Map<String, Node> nodes = new HashMap<>();
    private Map<String, Object> globalParameters = new HashMap<>();
    
    /**
     * 获取节点
     * 
     * @param nodeId 节点ID
     * @return 节点
     */
    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }
    
    /**
     * 添加节点
     * 
     * @param node 节点
     */
    public void addNode(Node node) {
        nodes.put(node.getId(), node);
    }
    
    /**
     * 获取全局参数
     * 
     * @param key 参数名
     * @return 参数值
     */
    public Object getGlobalParameter(String key) {
        return globalParameters.get(key);
    }
    
    /**
     * 设置全局参数
     * 
     * @param key 参数名
     * @param value 参数值
     */
    public void setGlobalParameter(String key, Object value) {
        globalParameters.put(key, value);
    }

    /**
     * Get all nodes that are ready to execute (all dependencies are completed)
     */
    public List<Node> getReadyNodes() {
        return nodes.values().stream()
                .filter(node -> node.getStatus() == Node.NodeStatus.PENDING && node.isReady(nodes))
                .collect(Collectors.toList());
    }

    /**
     * Check if all nodes in the DAG are completed
     */
    public boolean isCompleted() {
        return nodes.values().stream()
                .allMatch(node -> node.getStatus() == Node.NodeStatus.COMPLETED ||
                        node.getStatus() == Node.NodeStatus.SKIPPED ||
                        node.getStatus() == Node.NodeStatus.FAILED);
    }

    /**
     * Validate the DAG for cycles and missing dependencies
     */
    public boolean validate() {
        // Check for missing dependencies
        for (Node node : nodes.values()) {
            for (String depId : node.getDeps()) {
                if (!nodes.containsKey(depId)) {
                    return false;
                }
            }
        }

        // Check for cycles using DFS
        Map<String, Boolean> visited = new HashMap<>();
        Map<String, Boolean> recursionStack = new HashMap<>();

        for (String nodeId : nodes.keySet()) {
            if (hasCycle(nodeId, visited, recursionStack)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 基于拓扑排序验证是否存在环
     */
    private boolean hasCycle(String nodeId, Map<String, Boolean> visited, Map<String, Boolean> recursionStack) {
        if (recursionStack.getOrDefault(nodeId, false)) {
            return true;
        }
        if (visited.getOrDefault(nodeId, false)) {
            return false;
        }
        visited.put(nodeId, true);
        recursionStack.put(nodeId, true);
        Node node = nodes.get(nodeId);
        for (String depId : node.getDeps()) {
            if (hasCycle(depId, visited, recursionStack)) {
                return true;
            }
        }

        recursionStack.put(nodeId, false);
        return false;
    }
}

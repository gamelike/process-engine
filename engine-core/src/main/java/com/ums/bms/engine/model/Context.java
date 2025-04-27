package com.ums.bms.engine.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * 流程上下文
 */
@Data
public class Context {
    private Map<String, Object> globalParameters = new HashMap<>();
    private Map<String, Object> nodeResults = new HashMap<>();
    
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
     * 设置节点结果
     * 
     * @param nodeId 节点ID
     * @param result 结果
     */
    public void setNodeResult(String nodeId, Object result) {
        nodeResults.put(nodeId, result);
    }
    
    /**
     * 获取节点结果
     * 
     * @param nodeId 节点ID
     * @return 结果
     */
    public Object getNodeResult(String nodeId) {
        return nodeResults.get(nodeId);
    }
    
    /**
     * 获取前置节点结果
     * 
     * @param node 当前节点
     * @return 前置节点结果
     */
    public Object getPrevResult(Node node) {
        if (node.getDeps() == null || node.getDeps().isEmpty()) {
            return null;
        }
        return getNodeResult(node.getDeps().get(0));
    }
}

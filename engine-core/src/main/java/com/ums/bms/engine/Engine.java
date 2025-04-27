package com.ums.bms.engine;

import com.ums.bms.engine.model.Context;
import com.ums.bms.engine.model.DAGGraph;
import com.ums.bms.engine.model.Node;
import com.ums.bms.engine.parser.FlowConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author violet
 * @since 2025/4/27
 */
@Slf4j
public class Engine {

    /**
     * 执行流程
     *
     * @param flowConfig 流程配置
     * @return 执行结果
     */
    public Map<String, Object> execute(FlowConfig flowConfig) {
        Context ctx = new Context();
        DAGGraph dag = flowConfig.getDag();
        // 验证DAG是否有效
        if (!dag.validate()) {
            log.error("DAG validation failed for flow: {}", flowConfig.getName());
            throw new RuntimeException("Invalid DAG configuration");
        }
        // 执行DAG
        while (!dag.isCompleted()) {
            List<Node> readyNodes = dag.getReadyNodes();
            if (readyNodes.isEmpty() && !dag.isCompleted()) {
                log.error("No nodes are ready to execute, but DAG is not completed. Possible deadlock.");
                throw new RuntimeException("Execution deadlock detected");
            }

            // 执行所有就绪的节点
            for (Node node : readyNodes) {
                executeNode(node, ctx, dag, flowConfig.getGlobalParameters());
            }
        }
        return new HashMap<>(ctx.getNodeResults());
    }

    /**
     * 执行单个节点
     *
     * @param node             节点
     * @param dag              DAG
     * @param globalParameters 全局参数
     */
    private void executeNode(Node node, Context ctx, DAGGraph dag, Map<String, Object> globalParameters) {
        try {
            log.info("Executing node: {}", node.getId());
            node.setStatus(Node.NodeStatus.RUNNING);

            // 获取节点依赖的输入数据
            Map<String, Object> inputs = new HashMap<>();
            for (String depId : node.getDeps()) {
                inputs.put(depId, ctx.getNodeResults().get(depId));
            }

            // 根据节点类型执行不同的操作
            Object result = null;
            switch (node.getType()) {
                case SOURCE:
                    break;
                case PROCESSOR:
                    break;
                case SINK:
                    break;
                default:
                    throw new RuntimeException("Unknown node type: " + node.getType());
            }

            // 存储节点执行结果
            ctx.setNodeResult(node.getId(), result);
            node.setStatus(Node.NodeStatus.COMPLETED);
            log.info("Node completed: {}", node.getId());
        } catch (Exception e) {
            log.error("Error executing node: " + node.getId(), e);
            node.setStatus(Node.NodeStatus.FAILED);
            throw new RuntimeException("Error executing node: " + node.getId(), e);
        }
    }

}

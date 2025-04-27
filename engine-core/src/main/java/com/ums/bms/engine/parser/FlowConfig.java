package com.ums.bms.engine.parser;

import com.ums.bms.engine.model.DAGGraph;
import lombok.Data;

import java.util.Map;

/**
 * @author violet
 * @since 2025/4/23
 */
@Data
public class FlowConfig {
    /* 工作流名称 */
    private String name;
    /* 工作流版本 */
    private String version;
    /* 全局参数，每个节点应该都可以获取 */
    private Map<String, Object> globalParameters;
    /* 节点具体处理 */
    private DAGGraph dag;
}

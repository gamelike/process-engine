package com.ums.bms.engine.parser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ums.bms.engine.model.DAGGraph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author violet
 * @since 2025/4/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowConfig {
    private String id;
    /* 工作流名称 */
    private String name;
    /* 工作流版本 */
    private String version;
    /* 全局参数，每个节点应该都可以获取 */
    private Map<String, Object> globalParameters;
    /* 节点具体处理 */
    private DAGGraph dag;
}

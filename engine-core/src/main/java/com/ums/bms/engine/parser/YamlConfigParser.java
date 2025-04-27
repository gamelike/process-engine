package com.ums.bms.engine.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ums.bms.engine.exception.FlowConfigParserException;
import com.ums.bms.engine.model.DAGGraph;
import com.ums.bms.engine.model.Node;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Parser for YAML configuration files.
 */
@Slf4j
public class YamlConfigParser {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper(new YAMLFactory());
    }

    /**
     * Parse a YAML configuration file into a FlowConfig object
     */
    public static FlowConfig parse(File configFile) {
        try {
            Map<String, Object> config = mapper.readValue(configFile, new TypeReference<>() {
            });
            return parseConfig(config);
        } catch (IOException e) {
            throw new FlowConfigParserException("Error parsing YAML configuration", e);
        }
    }

    /**
     * Parse a configuration map into a FlowConfig object
     */
    @SuppressWarnings("unchecked")
    private static FlowConfig parseConfig(Map<String, Object> config) {
        FlowConfig flowConfig = new FlowConfig();
        // Parse basic properties
        flowConfig.setName((String) config.get("name"));
        flowConfig.setVersion((String) config.get("version"));
        // Parse global parameters
        Map<String, Object> globalParams = (Map<String, Object>) config.get("global-parameters");
        flowConfig.setGlobalParameters(Objects.requireNonNullElseGet(globalParams, HashMap::new));
        // Parse DAGs
        Map<String, Object> dagConfig = (Map<String, Object>) config.get("dag");
        if (dagConfig != null) {
            DAGGraph dag = parseDag(dagConfig);
            flowConfig.setDag(dag);
        }
        return flowConfig;
    }

    /**
     * Parse a DAG configuration map into a Dag object
     */
    @SuppressWarnings("unchecked")
    private static DAGGraph parseDag(Map<String, Object> dagConfig) {
        DAGGraph dag = new DAGGraph();
        // Parse basic properties
        dag.setId((String) dagConfig.get("id"));
        // Parse global parameters
        Map<String, Object> globalParams = (Map<String, Object>) dagConfig.get("globalParameters");
        if (globalParams != null) {
            dag.setGlobalParameters(globalParams);
        }
        // Parse nodes
        List<Map<String, Object>> nodesConfig = (List<Map<String, Object>>) dagConfig.get("nodes");
        if (nodesConfig != null) {
            log.warn("当前工作流: {} 为空，不包含处理节点.", dag.getId());
            for (Map<String, Object> nodeConfig : nodesConfig) {
                Node node = parseNode(nodeConfig);
                dag.addNode(node);
            }
        }
        return dag;
    }

    /**
     * Parse a node configuration map into a Node object
     */
    private static Node parseNode(Map<String, Object> nodeConfig) {
        return mapper.convertValue(nodeConfig, Node.class);
    }
}

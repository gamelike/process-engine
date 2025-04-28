package com.ums.bms.engine.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ums.bms.engine.exception.FlowConfigParserException;
import com.ums.bms.engine.model.DAGGraph;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * Parser for YAML configuration files.
 */
@Slf4j
public class YamlConfigParser {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper(new YAMLFactory());
    }

    private YamlConfigParser() {}

    /**
     * Parse a YAML configuration file into a FlowConfig object
     */
    public static FlowConfig parse(File configFile) {
        try {
            FlowConfig flowConfig = mapper.readValue(configFile, FlowConfig.class);
            valid(flowConfig);
            return flowConfig;
        } catch (IOException e) {
            throw new FlowConfigParserException(
                    "解析流程配置错误，文件路径为" + configFile.getAbsolutePath(), e
            );
        }
    }

    private static void valid(@NonNull FlowConfig config) {
        if (config.getId() == null || config.getId().isBlank()) {
            throw new FlowConfigParserException("流程配置ID为空");
        }
        if (config.getName() == null || config.getName().isBlank()) {
            throw new FlowConfigParserException("流程配置名称为必填项.");
        }
        if (config.getVersion() == null || config.getVersion().isBlank()) {
            throw new FlowConfigParserException("流程配置版本为必填项.");
        }
        DAGGraph dag = config.getDag();
        if (dag.getNodes() == null || dag.getNodes().isEmpty()) {
            throw new FlowConfigParserException("流程配置节点为空.");
        }
        if (!dag.validate()) {
            throw new FlowConfigParserException("流程配置存在环.");
        }
    }

}
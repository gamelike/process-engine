package com.ums.bms.engine.parser;

import com.ums.bms.engine.exception.FlowConfigParserException;
import com.ums.bms.engine.model.DAGGraph;
import com.ums.bms.engine.model.Node;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <th></th>测试问题:
 *
 *
 * @author violet
 * @since 2025/04/28
 */
@ExtendWith(MockitoExtension.class)
public class YamlConfigParserTest {

    @TempDir
    Path tempDir;

    @Test
    void parse_ValidConfig_ReturnsFlowConfig() throws IOException {
        // Arrange
        File configFile = createValidConfigFile();

        // Act
        FlowConfig result = YamlConfigParser.parse(configFile);

        // Assert
        assertNotNull(result);
        assertEquals("test-flow", result.getId());
        assertEquals("testFlow", result.getName());
        assertEquals("1.0.0", result.getVersion());
        assertNotNull(result.getDag());
    }

    @Test
    void parse_InvalidYamlFile_ThrowsException() throws IOException {
        // Arrange
        File configFile = tempDir.resolve("invalid.yaml").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("invalid: yaml: content: :");
        }

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(configFile)
        );
        assertTrue(exception.getMessage().contains(configFile.getAbsolutePath()));
    }

    @Test
    void parse_NonexistentFile_ThrowsException() {
        // Arrange
        File nonexistentFile = new File("nonexistent.yaml");

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(nonexistentFile)
        );
        assertTrue(exception.getMessage().contains(nonexistentFile.getAbsolutePath()));
    }

    @Test
    void valid_NullConfig_ThrowsException() throws IOException {
        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(createConfigFile(null))
        );
        Assertions.assertThat(exception.getMessage()).contains("解析流程配置错误");
    }

    @Test
    void valid_EmptyId_ThrowsException() throws IOException {
        // Arrange
        FlowConfig config = createValidFlowConfig();
        config.setId("");

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(createConfigFile(config))
        );
        assertEquals("流程配置ID为空", exception.getMessage());
    }

    @Test
    void valid_EmptyName_ThrowsException() throws IOException {
        // Arrange
        FlowConfig config = createValidFlowConfig();
        config.setName("");

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(createConfigFile(config))
        );
        assertEquals("流程配置名称为必填项.", exception.getMessage());
    }

    @Test
    void valid_EmptyVersion_ThrowsException() throws IOException {
        // Arrange
        FlowConfig config = createValidFlowConfig();
        config.setVersion("");

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(createConfigFile(config))
        );
        assertEquals("流程配置版本为必填项.", exception.getMessage());
    }

    @Test
    void valid_EmptyNodes_ThrowsException() throws IOException {
        // Arrange
        File configFile = tempDir.resolve("empty-nodes.yaml").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("""
                id: test-flow
                name: testFlow
                version: 1.0.0
                dag:
                  nodes: []
                """);
        }

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(configFile)
        );
        assertEquals("流程配置节点为空.", exception.getMessage());
    }

    @Test
    void valid_CyclicDag_ThrowsException() throws IOException {
        // Arrange
        File configFile = tempDir.resolve("cyclic-dag.yaml").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("""
                id: test-flow
                name: testFlow
                version: 1.0.0
                dag:
                  nodes:
                    - id: node1
                      name: Node 1
                      type: PROCESSOR
                      deps: ["node2"]
                    - id: node2
                      name: Node 2
                      type: PROCESSOR
                      deps: ["node1"]
                """);
        }

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(configFile)
        );
        assertEquals("流程配置存在环.", exception.getMessage());
    }

    @Test
    void parse_ValidConfigWithMultipleNodes_ReturnsFlowConfig() throws IOException {
        // Arrange
        File configFile = tempDir.resolve("multi-nodes.yaml").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("""
                id: test-flow
                name: testFlow
                version: 1.0.0
                dag:
                  nodes:
                    - id: node1
                      name: Node 1
                      type: PROCESSOR
                      parameters:
                        param1: value1
                    - id: node2
                      name: Node 2
                      type: PROCESSOR
                      deps: ["node1"]
                      parameters:
                        param2: value2
                """);
        }

        // Act
        FlowConfig result = YamlConfigParser.parse(configFile);

        // Assert
        assertNotNull(result);
        assertEquals("test-flow", result.getId());
        assertEquals("testFlow", result.getName());
        assertEquals("1.0.0", result.getVersion());
        assertNotNull(result.getDag());
        assertEquals(2, result.getDag().getNodes().size());
        assertTrue(result.getDag().validate());
    }

    @Test
    void parse_MalformedYamlFile_ThrowsException() throws IOException {
        // Arrange
        File configFile = tempDir.resolve("malformed.yaml").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("""
                id: test-flow
                name: testFlow
                version: 1.0.0
                dag:
                  nodes:
                    - id: node1
                      name: Node 1
                    type: PROCESSOR  # 故意的缩进错误
                """);
        }

        // Act & Assert
        assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(configFile)
        );
    }

    @Test
    void valid_BlankId_ThrowsException() throws IOException {
        // Arrange
        FlowConfig config = createValidFlowConfig();
        config.setId("   ");  // 全是空格

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(createConfigFile(config))
        );
        assertEquals("流程配置ID为空", exception.getMessage());
    }

    @Test
    void valid_BlankName_ThrowsException() throws IOException {
        // Arrange
        FlowConfig config = createValidFlowConfig();
        config.setId("asdf");
        config.setVersion("1.0.0");
        config.setName("  ");  // 制表符和换行符

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(createConfigFile(config))
        );
        assertEquals("流程配置名称为必填项.", exception.getMessage());
    }

    @Test
    void valid_BlankVersion_ThrowsException() throws IOException {
        // Arrange
        FlowConfig config = createValidFlowConfig();
        config.setId("asdf");
        config.setName("sgdsa");
        config.setVersion("   ");  // 混合空白字符

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(createConfigFile(config))
        );
        assertEquals("流程配置版本为必填项.", exception.getMessage());
    }

    @Test
    void parse_ConfigWithBlankValues_ThrowsException() throws IOException {
        // Arrange
        File configFile = tempDir.resolve("blank-values.yaml").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("""
                id: "   "
                name: "  "
                version: "  "
                dag:
                  nodes:
                    - id: node1
                      name: Test Node
                      type: PROCESSOR
                """);
        }

        // Act & Assert
        FlowConfigParserException exception = assertThrows(
            FlowConfigParserException.class,
            () -> YamlConfigParser.parse(configFile)
        );
        assertEquals("流程配置ID为空", exception.getMessage());
    }

    private File createValidConfigFile() throws IOException {
        File configFile = tempDir.resolve("valid-config.yaml").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("""
                id: test-flow
                name: testFlow
                version: 1.0.0
                dag:
                  nodes:
                    - id: node1
                      name: Test Node
                      type: PROCESSOR
                """);
        }
        return configFile;
    }

    private FlowConfig createValidFlowConfig() {
        FlowConfig config = new FlowConfig();
        config.setId("test-flow");
        config.setName("testFlow");
        config.setVersion("1.0.0");

        DAGGraph dag = new DAGGraph();
        Node node = new Node();
        node.setId("node1");
        node.setName("Test Node");
        Map<String, Node> nodes = new HashMap<>();
        nodes.put(node.getId(), node);
        dag.setNodes(nodes);

        config.setDag(dag);
        return config;
    }

    private File createConfigFile(FlowConfig config) throws IOException {
        File configFile = tempDir.resolve("test-config.yaml").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            if (config == null) {
                writer.write("");
            } else {
                writer.write(String.format("""
                    id: %s
                    name: %s
                    version: %s
                    dag:
                      nodes:
                        - id: node1
                          name: Test Node
                          type: PROCESSOR
                    """, config.getId(), config.getName(), config.getVersion()));
            }
        }
        return configFile;
    }
}
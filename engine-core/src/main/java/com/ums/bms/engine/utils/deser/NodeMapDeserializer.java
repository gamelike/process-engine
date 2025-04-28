package com.ums.bms.engine.utils.deser;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ums.bms.engine.model.Node;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定制的反序列化器
 *
 * @author violet
 * @since 2025/4/28
 */
public class NodeMapDeserializer extends JsonDeserializer<Map<String, Node>> {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Override
    public Map<String, Node> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        List<Node> nodes = mapper.readValue(p, new TypeReference<>() {
        });
        return nodes.stream().collect(Collectors.toMap(Node::getId, node -> node));
    }
}

package model.level.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import model.level.Level;
import model.level.LevelLoadException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonLevelLoader implements LevelLoader {
    private static final int DEFAULT_MAX_MOVES = 50;
    private static final Set<String> EDGE_SPEC_FIELDS = Set.of("nodeA", "nodeB", "type");

    @Override
    public Level load(String filePath) throws IOException, LevelLoadException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or blank");
        }

        try (Reader reader = new FileReader(filePath)) {
            JsonObject schema = JsonParser.parseReader(reader).getAsJsonObject();
            return toLevel(schema);
        } catch (JsonSyntaxException e) {
            throw new LevelLoadException("Invalid JSON: " + e.getMessage());
        }
    }

    private Level toLevel(JsonObject schema) throws LevelLoadException {
        if (schema == null || !schema.has("nodes") || !schema.get("nodes").isJsonArray()
            || schema.getAsJsonArray("nodes").isEmpty()) {
            throw new LevelLoadException("Level must have at least one node");
        }

        int maxMoves = readPositiveInt(schema, "maxMoves", DEFAULT_MAX_MOVES);

        JsonArray nodeSchemas = schema.getAsJsonArray("nodes");
        List<Level.NodeData> nodes = new ArrayList<>(nodeSchemas.size());
        for (JsonElement nodeElement : nodeSchemas) {
            if (!nodeElement.isJsonObject()) {
                throw new LevelLoadException("Node must be an object");
            }
            JsonObject node = nodeElement.getAsJsonObject();
            validateNode(node);
            nodes.add(new Level.NodeData(
                node.get("x").getAsDouble(),
                node.get("y").getAsDouble()
            ));
        }

        List<Level.EdgeSpec> edgeSpecs = new ArrayList<>();
        JsonArray edgeSchemas = edgeSchemas(schema);
        if (edgeSchemas != null) {
            for (JsonElement edgeElement : edgeSchemas) {
                if (!edgeElement.isJsonObject()) {
                    throw new LevelLoadException("Edge must be an object");
                }
                JsonObject edge = edgeElement.getAsJsonObject();
                validateEdge(edge, nodes.size());
                edgeSpecs.add(toEdgeSpec(edge));
            }
        }

        return new Level(maxMoves, nodes, edgeSpecs);
    }

    private Level.EdgeSpec toEdgeSpec(JsonObject edge) {
        Map<String, Double> parameters = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : edge.entrySet()) {
            String fieldName = entry.getKey();
            JsonElement value = entry.getValue();
            if (!EDGE_SPEC_FIELDS.contains(fieldName) && isNumber(value)) {
                parameters.put(fieldName, value.getAsDouble());
            }
        }

        return new Level.EdgeSpec(
            edge.get("nodeA").getAsInt(),
            edge.get("nodeB").getAsInt(),
            edge.has("type") && !edge.get("type").isJsonNull() ? edge.get("type").getAsString() : null,
            parameters
        );
    }

    private void validateNode(JsonObject node) throws LevelLoadException {
        if (!isNumber(node.get("x")) || !isNumber(node.get("y"))) {
            throw new LevelLoadException("Node must have x and y coordinates");
        }
    }

    private void validateEdge(JsonObject edge, int nodeCount) throws LevelLoadException {
        if (!isNumber(edge.get("nodeA")) || !isNumber(edge.get("nodeB"))) {
            throw new LevelLoadException("Edge must have nodeA and nodeB indices");
        }

        int nodeA = edge.get("nodeA").getAsInt();
        int nodeB = edge.get("nodeB").getAsInt();

        if (nodeA < 0 || nodeB < 0
            || nodeA >= nodeCount || nodeB >= nodeCount) {
            throw new LevelLoadException(
                String.format("Invalid edge indices (%d, %d) for %d nodes",
                    nodeA, nodeB, nodeCount));
        }
    }

    private JsonArray edgeSchemas(JsonObject schema) {
        if (schema.has("edges") && schema.get("edges").isJsonArray()
            && !schema.getAsJsonArray("edges").isEmpty()) {
            return schema.getAsJsonArray("edges");
        }
        if (schema.has("edgeSpecs") && schema.get("edgeSpecs").isJsonArray()) {
            return schema.getAsJsonArray("edgeSpecs");
        }
        return null;
    }

    private int readPositiveInt(JsonObject object, String fieldName, int defaultValue) {
        JsonElement value = object.get(fieldName);
        if (!isNumber(value)) {
            return defaultValue;
        }

        int resolved = value.getAsInt();
        return resolved > 0 ? resolved : defaultValue;
    }

    private boolean isNumber(JsonElement element) {
        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()) {
            return false;
        }

        JsonPrimitive primitive = element.getAsJsonPrimitive();
        return primitive.isNumber();
    }
}

package model.level.loader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.level.Level;
import model.level.LevelLoadException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class JsonLevelLoader implements LevelLoader {
    private static final int DEFAULT_MAX_MOVES = 50;
    private final Gson _gson = new Gson();

    @Override
    public Level load(String filePath) throws IOException, LevelLoadException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or blank");
        }

        try (Reader reader = new FileReader(filePath)) {
            LevelSchema schema = _gson.fromJson(reader, LevelSchema.class);
            return toLevel(schema);
        } catch (JsonSyntaxException e) {
            throw new LevelLoadException("Invalid JSON: " + e.getMessage());
        }
    }

    private Level toLevel(LevelSchema schema) throws LevelLoadException {
        if (schema.nodes == null || schema.nodes.isEmpty()) {
            throw new LevelLoadException("Level must have at least one node");
        }

        int maxMoves = (schema.maxMoves != null && schema.maxMoves > 0)
            ? schema.maxMoves : DEFAULT_MAX_MOVES;

        List<Level.NodeData> nodes = new ArrayList<>(schema.nodes.size());
        for (NodeSchema node : schema.nodes) {
            validateNode(node);
            nodes.add(new Level.NodeData(node.x, node.y));
        }

        List<Level.EdgeData> edges = new ArrayList<>();
        if (schema.edges != null) {
            for (EdgeSchema edge : schema.edges) {
                validateEdge(edge, nodes.size());
                edges.add(new Level.EdgeData(edge.nodeA, edge.nodeB));
            }
        }

        return new Level(maxMoves, nodes, edges);
    }

    private void validateNode(NodeSchema node) throws LevelLoadException {
        if (node.x == null || node.y == null) {
            throw new LevelLoadException("Node must have x and y coordinates");
        }
    }

    private void validateEdge(EdgeSchema edge, int nodeCount) throws LevelLoadException {
        if (edge.nodeA == null || edge.nodeB == null) {
            throw new LevelLoadException("Edge must have nodeA and nodeB indices");
        }
        if (edge.nodeA < 0 || edge.nodeB < 0
            || edge.nodeA >= nodeCount || edge.nodeB >= nodeCount) {
            throw new LevelLoadException(
                String.format("Invalid edge indices (%d, %d) for %d nodes",
                    edge.nodeA, edge.nodeB, nodeCount));
        }
    }

    private static class LevelSchema {
        Integer maxMoves;
        List<NodeSchema> nodes;
        List<EdgeSchema> edges;
    }

    private static class NodeSchema {
        Double x, y;
    }

    private static class EdgeSchema {
        Integer nodeA, nodeB;
    }
}

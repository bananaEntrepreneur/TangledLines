package model.level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {
    private final int _maxMoves;
    private final List<NodeData> _nodes;
    private final List<EdgeData> _edges;
    private final List<EdgeSpec> _edgeSpecs;

    public Level(int maxMoves, List<NodeData> nodes, List<EdgeData> edges) {
        this(maxMoves, nodes, edges, edges == null ? null : edges.stream()
            .map(edge -> new EdgeSpec(
                edge.nodeAIndex(),
                edge.nodeBIndex(),
                EdgeKind.BASIC,
                null,
                null
            ))
            .toList());
    }

    public Level(int maxMoves, List<NodeData> nodes, List<EdgeData> edges, List<EdgeSpec> edgeSpecs) {
        if (maxMoves < 1) {
            throw new IllegalArgumentException("maxMoves must be at least 1, got: " + maxMoves);
        }
        _maxMoves = maxMoves;

        if (nodes == null) {
            throw new IllegalArgumentException("nodes list cannot be null");
        }
        _nodes = List.copyOf(nodes);

        if (edges == null) {
            throw new IllegalArgumentException("edges list cannot be null");
        }
        _edges = List.copyOf(edges);

        if (edgeSpecs == null) {
            throw new IllegalArgumentException("edgeSpecs list cannot be null");
        }
        _edgeSpecs = List.copyOf(edgeSpecs);
    }

    public int getMaxMoves() { return _maxMoves; }
    public List<NodeData> getNodes() { return _nodes; }
    public List<EdgeData> getEdges() { return _edges; }
    public List<EdgeSpec> getEdgeSpecs() { return _edgeSpecs; }

    public record NodeData(double x, double y) {}
    public record EdgeData(int nodeAIndex, int nodeBIndex) {}
    public record EdgeSpec(int nodeAIndex, int nodeBIndex, String type, Map<String, Double> parameters) {
        public EdgeSpec {
            type = normalizeType(type);
            parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
        }

        public EdgeSpec(int nodeAIndex, int nodeBIndex, EdgeKind kind, Double stretchPercent, Double breakPercent) {
            this(
                nodeAIndex,
                nodeBIndex,
                kind == null ? EdgeKind.BASIC.name().toLowerCase() : kind.name().toLowerCase(),
                legacyParameters(stretchPercent, breakPercent)
            );
        }

        public EdgeKind kind() {
            try {
                return EdgeKind.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public Double stretchPercent() {
            return parameters.get("stretchPercent");
        }

        public Double breakPercent() {
            return parameters.get("breakPercent");
        }

        private static String normalizeType(String type) {
            if (type == null || type.isBlank()) {
                return EdgeKind.BASIC.name().toLowerCase();
            }
            return type.trim().toLowerCase();
        }

        private static Map<String, Double> legacyParameters(Double stretchPercent, Double breakPercent) {
            Map<String, Double> parameters = new HashMap<>();
            if (stretchPercent != null) {
                parameters.put("stretchPercent", stretchPercent);
            }
            if (breakPercent != null) {
                parameters.put("breakPercent", breakPercent);
            }
            return parameters;
        }
    }
    public enum EdgeKind { BASIC, STRETCHABLE, BREAKABLE }
}

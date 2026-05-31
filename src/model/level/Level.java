package model.level;

import java.util.List;
import java.util.Map;

public class Level {
    private final int _maxMoves;
    private final List<NodeData> _nodes;
    private final List<EdgeSpec> _edgeSpecs;

    public Level(int maxMoves, List<NodeData> nodes, List<EdgeSpec> edgeSpecs) {
        if (maxMoves < 1) {
            throw new IllegalArgumentException("maxMoves must be at least 1, got: " + maxMoves);
        }
        _maxMoves = maxMoves;

        if (nodes == null) {
            throw new IllegalArgumentException("nodes list cannot be null");
        }
        _nodes = List.copyOf(nodes);

        if (edgeSpecs == null) {
            throw new IllegalArgumentException("edgeSpecs list cannot be null");
        }
        _edgeSpecs = List.copyOf(edgeSpecs);
    }

    public int getMaxMoves() { return _maxMoves; }
    public List<NodeData> getNodes() { return _nodes; }
    public List<EdgeSpec> getEdgeSpecs() { return _edgeSpecs; }

    public record NodeData(double x, double y) {}
    public record EdgeSpec(int nodeAIndex, int nodeBIndex, String type, EdgeParameters parameters) {
        public EdgeSpec(int nodeAIndex, int nodeBIndex, String type, Map<String, Double> parameters) {
            this(nodeAIndex, nodeBIndex, type, new EdgeParameters(parameters));
        }

        public EdgeSpec {
            type = normalizeType(type);
            parameters = parameters == null ? new EdgeParameters(Map.of()) : parameters;
        }

        private static String normalizeType(String type) {
            if (type == null || type.isBlank()) {
                return "basic";
            }
            return type.trim().toLowerCase();
        }
    }

    public record EdgeParameters(Map<String, Double> values) {
        public EdgeParameters {
            values = values == null ? Map.of() : Map.copyOf(values);
        }

        public Double get(EdgeParameter parameter) {
            return values.get(parameter.jsonName());
        }

        public double getOrDefault(EdgeParameter parameter, double defaultValue) {
            Double value = get(parameter);
            return value == null ? defaultValue : value;
        }

        public Double getCustom(String parameterName) {
            return values.get(parameterName);
        }

        public double getCustomOrDefault(String parameterName, double defaultValue) {
            Double value = getCustom(parameterName);
            return value == null ? defaultValue : value;
        }
    }

    public enum EdgeParameter {
        STRETCH_PERCENT("stretchPercent"),
        BREAK_PERCENT("breakPercent");

        private final String _jsonName;

        EdgeParameter(String jsonName) {
            _jsonName = jsonName;
        }

        public String jsonName() {
            return _jsonName;
        }
    }
}

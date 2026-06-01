package model.level;

import model.game.Field;
import model.units.Edge;
import model.units.Node;

import java.util.HashMap;
import java.util.Map;

public final class EdgeTypeRegistry {
    private final Map<String, EdgeTypeFactory> _factories = new HashMap<>();

    public static EdgeTypeRegistry withDefaults() {
        return new EdgeTypeRegistry()
            .register(new BasicEdgeFactory())
            .register(new StretchableEdgeFactory())
            .register(new BreakableEdgeFactory())
            .register(new OverheatingEdgeFactory());
    }

    public EdgeTypeRegistry register(EdgeTypeFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Edge type factory cannot be null");
        }

        String type = normalizeType(factory.getType());
        if (type.isBlank()) {
            throw new IllegalArgumentException("Edge type cannot be blank");
        }

        _factories.put(type, factory);
        return this;
    }

    public Edge createEdge(Field field, Node nodeA, Node nodeB, Level.EdgeSpec spec) {
        if (spec == null) {
            throw new IllegalArgumentException("Edge spec cannot be null");
        }

        EdgeTypeFactory factory = _factories.get(normalizeType(spec.type()));
        if (factory == null) {
            throw new IllegalArgumentException("Unknown edge type: " + spec.type());
        }

        return factory.createEdge(field, nodeA, nodeB, spec);
    }

    private static String normalizeType(String type) {
        return type == null ? "" : type.trim().toLowerCase();
    }

    private static double percent(Level.EdgeSpec spec, Level.EdgeParameter parameter, double defaultValue) {
        double resolved = spec.parameters().getOrDefault(parameter, defaultValue);
        if (resolved < 0) {
            throw new IllegalArgumentException(parameter.jsonName() + " must be non-negative");
        }
        return resolved;
    }

    private static double positiveParameter(Level.EdgeSpec spec, String parameterName, double defaultValue) {
        double resolved = spec.parameters().getCustomOrDefault(parameterName, defaultValue);
        if (resolved <= 0) {
            throw new IllegalArgumentException(parameterName + " must be positive");
        }
        return resolved;
    }

    private static class BasicEdgeFactory implements EdgeTypeFactory {
        @Override
        public String getType() {
            return "basic";
        }

        @Override
        public Edge createEdge(Field field, Node nodeA, Node nodeB, Level.EdgeSpec spec) {
            return field.createEdge(nodeA, nodeB);
        }
    }

    private static class StretchableEdgeFactory implements EdgeTypeFactory {
        @Override
        public String getType() {
            return "stretchable";
        }

        @Override
        public Edge createEdge(Field field, Node nodeA, Node nodeB, Level.EdgeSpec spec) {
            return field.createStretchableEdge(
                nodeA,
                nodeB,
                percent(spec, Level.EdgeParameter.STRETCH_PERCENT, 25.0)
            );
        }
    }

    private static class BreakableEdgeFactory implements EdgeTypeFactory {
        @Override
        public String getType() {
            return "breakable";
        }

        @Override
        public Edge createEdge(Field field, Node nodeA, Node nodeB, Level.EdgeSpec spec) {
            return field.createBreakableEdge(
                nodeA,
                nodeB,
                percent(spec, Level.EdgeParameter.BREAK_PERCENT, 150.0)
            );
        }
    }

    private static class OverheatingEdgeFactory implements EdgeTypeFactory {
        @Override
        public String getType() {
            return "overheating";
        }

        @Override
        public Edge createEdge(Field field, Node nodeA, Node nodeB, Level.EdgeSpec spec) {
            return field.createOverheatingEdge(
                nodeA,
                nodeB,
                positiveParameter(spec, "heatPerIntersection", 25.0),
                positiveParameter(spec, "coolPerMove", 10.0),
                positiveParameter(spec, "criticalHeat", 100.0)
            );
        }
    }
}

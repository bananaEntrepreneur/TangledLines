package model.level;

import model.game.Field;
import model.units.Node;

import java.awt.geom.Point2D;
import java.util.List;

public class LevelFactory {
    public Field createField(Level level) {
        Field field = getField(level);

        List<Node> nodes = field.getNodes();
        List<Level.EdgeSpec> edgeSpecs = level.getEdgeSpecs().isEmpty()
            ? level.getEdges().stream()
                .map(edge -> new Level.EdgeSpec(
                    edge.nodeAIndex(),
                    edge.nodeBIndex(),
                    Level.EdgeKind.BASIC,
                    null,
                    null
                ))
                .toList()
            : level.getEdgeSpecs();

        for (Level.EdgeSpec spec : edgeSpecs) {
            validateEdgeIndex(spec.nodeAIndex(), nodes.size());
            validateEdgeIndex(spec.nodeBIndex(), nodes.size());

            Node nodeA = nodes.get(spec.nodeAIndex());
            Node nodeB = nodes.get(spec.nodeBIndex());

            Level.EdgeKind kind = spec.kind() == null ? Level.EdgeKind.BASIC : spec.kind();
            switch (kind) {
                case STRETCHABLE -> field.createStretchableEdge(nodeA, nodeB, requirePercent(spec.stretchPercent(), 25.0));
                case BREAKABLE -> field.createBreakableEdge(nodeA, nodeB, requirePercent(spec.breakPercent(), 150.0));
                default -> field.createEdge(nodeA, nodeB);
            }
        }

        return field;
    }

    private double requirePercent(Double value, double defaultValue) {
        double resolved = value == null ? defaultValue : value;
        if (resolved < 0) {
            throw new IllegalArgumentException("Percent values must be non-negative");
        }
        return resolved;
    }

    private static Field getField(Level level) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }

        List<Level.NodeData> nodeDataList = level.getNodes();
        if (nodeDataList.isEmpty()) {
            throw new IllegalArgumentException("Level must have at least one node");
        }

        Field field = new Field();
        for (Level.NodeData data : nodeDataList) {
            Point2D position = new Point2D.Double(data.x(), data.y());
            field.createNode(position);
        }
        return field;
    }

    private void validateEdgeIndex(int index, int nodeCount) {
        if (index < 0 || index >= nodeCount) {
            throw new IllegalArgumentException(
                String.format("Invalid edge: %d (node count: %d)", index, nodeCount));
        }
    }
}

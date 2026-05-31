package model.level;

import model.game.Field;
import model.units.Node;

import java.awt.geom.Point2D;
import java.util.List;

public class LevelFactory {
    private final EdgeTypeRegistry _edgeTypes;

    public LevelFactory() {
        this(EdgeTypeRegistry.withDefaults());
    }

    public LevelFactory(EdgeTypeRegistry edgeTypes) {
        if (edgeTypes == null) {
            throw new IllegalArgumentException("Edge type registry cannot be null");
        }
        _edgeTypes = edgeTypes;
    }

    public Field createField(Level level) {
        Field field = getField(level);

        List<Node> nodes = field.getNodes();
        for (Level.EdgeSpec spec : level.getEdgeSpecs()) {
            validateEdgeIndex(spec.nodeAIndex(), nodes.size());
            validateEdgeIndex(spec.nodeBIndex(), nodes.size());

            Node nodeA = nodes.get(spec.nodeAIndex());
            Node nodeB = nodes.get(spec.nodeBIndex());

            _edgeTypes.createEdge(field, nodeA, nodeB, spec);
        }

        return field;
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

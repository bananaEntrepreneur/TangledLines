package model.level.factory;

import model.game.Field;
import model.level.Level;
import model.units.Node;

import java.awt.geom.Point2D;
import java.util.List;

public class LevelFactory {
    public Field createField(Level level) {
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

        List<Node> nodes = field.getNodes();
        for (Level.EdgeData data : level.getEdges()) {
            validateEdgeIndex(data.nodeAIndex(), nodes.size());
            validateEdgeIndex(data.nodeBIndex(), nodes.size());
            field.createEdge(nodes.get(data.nodeAIndex()), nodes.get(data.nodeBIndex()));
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

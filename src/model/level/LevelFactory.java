package model.level;

import model.factory.UnitFactory;
import model.game.Field;
import model.units.Node;

import java.awt.geom.Point2D;
import java.util.List;

public class LevelFactory {
    private final UnitFactory _unitFactory;

    public LevelFactory(UnitFactory unitFactory) {
        if (unitFactory == null) {
            throw new IllegalArgumentException("UnitFactory cannot be null");
        }
        _unitFactory = unitFactory;
    }

    public Field createField(Level level) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }

        Field field = new Field(_unitFactory);
        for (Level.NodeData data : level.getNodes()) {
            Point2D position = new Point2D.Double(data.x(), data.y());
            field.createNode(position, data.movable());
        }

        List<Node> nodes = field.getNodes();
        for (Level.EdgeData data : level.getEdges()) {
            field.createEdge(nodes.get(data.nodeAIndex()),
                nodes.get(data.nodeBIndex()));
        }

        return field;
    }
}

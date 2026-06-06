package model.level.seeder;

import model.game.Field;
import model.level.Level;
import model.units.BreakableEdge;
import model.units.Edge;
import model.units.Node;
import model.units.OverheatingEdge;
import model.units.StretchableEdge;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Supplier;

public abstract class Seeder {
    public abstract List<Level> seed();

    protected Level level(int maxMoveCount, Supplier<Field> fieldSupplier) {
        return new Level(maxMoveCount, fieldSupplier);
    }

    protected Field field() {
        return new Field();
    }

    protected Node node(Field field, double x, double y) {
        return field.createNode(new Point2D.Double(x, y));
    }

    protected Edge edge(Field field, Node nodeA, Node nodeB) {
        return field.createEdge(nodeA, nodeB);
    }

    protected StretchableEdge stretchableEdge(
            Field field,
            Node nodeA,
            Node nodeB,
            double stretchPercent
    ) {
        return field.createStretchableEdge(nodeA, nodeB, stretchPercent);
    }

    protected BreakableEdge breakableEdge(
            Field field,
            Node nodeA,
            Node nodeB,
            double breakPercent
    ) {
        return field.createBreakableEdge(nodeA, nodeB, breakPercent);
    }

    protected OverheatingEdge overheatingEdge(
            Field field,
            Node nodeA,
            Node nodeB,
            double heatPerIntersection,
            double coolPerMove,
            double criticalHeat
    ) {
        return field.createOverheatingEdge(
            nodeA,
            nodeB,
            heatPerIntersection,
            coolPerMove,
            criticalHeat
        );
    }
}

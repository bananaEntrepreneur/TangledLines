package model.game;

import model.units.Node;

import java.awt.geom.Point2D;

public class Game {
    private int moveCount;
    private final Field _field;

    public Game(Field field) {
        _field = field;
        moveCount = 0;
    }

    public Field getField() {
        return _field;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void start() { }

    public boolean moveNode(Node node, Point2D newPosition) {
        boolean moved = _field.moveNodeTo(node, newPosition);
        if (moved) {
            moveCount++;
        }
        return moved;
    }

    public boolean hasWon() {
        return !_field.hasIntersections();
    }
}

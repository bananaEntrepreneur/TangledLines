package model.units;

import java.awt.geom.Point2D;

public class Node {
    private Point2D _position;
    private final boolean _movable;

    Node(Point2D position, boolean movable) {
        _position = position;
        _movable = movable;
    }

    public Point2D getPosition() {
        return _position;
    }

    public boolean isMovable() {
        return _movable;
    }

    private void setPosition(Point2D newPosition) {
        _position = newPosition;
    }
}

package model.units;

import model.listeners.NodeListener;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Node {
    private static final double MIN_MOVE_DISTANCE_SQUARED = 1.0;

    private Point2D _position;
    private final boolean _movable;
    private final List<NodeListener> _listeners = new ArrayList<>();

    public Node(Point2D position, boolean movable) {
        if (position == null) {
            throw new IllegalArgumentException("Node position cannot be null");
        }
        _position = position;
        _movable = movable;
    }

    public boolean move(Point2D newPosition) {
        if (!canMoveTo(newPosition)) {
            return false;
        }
        _position = newPosition;
        notifyListeners(newPosition);
        return true;
    }

    private boolean canMoveTo(Point2D newPosition) {
        if (!_movable || newPosition == null || newPosition.equals(_position)) {
            return false;
        }
        return distanceSquaredTo(newPosition) >= MIN_MOVE_DISTANCE_SQUARED;
    }

    private double distanceSquaredTo(Point2D target) {
        double dx = _position.getX() - target.getX();
        double dy = _position.getY() - target.getY();
        return dx * dx + dy * dy;
    }

    private void setPosition(Point2D newPosition) {
        if (!_movable || newPosition == null || newPosition.equals(_position)) {
            return;
        }
        _position = newPosition;
        notifyListeners(newPosition);
    }

    public Point2D getPosition() { return _position; }

    public boolean isMovable() { return _movable; }

    public void addListener(NodeListener listener) { _listeners.add(listener); }

    public void removeListener(NodeListener listener) { _listeners.remove(listener); }

    public List<NodeListener> getListeners() { return Collections.unmodifiableList(_listeners); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return _position.equals(node._position) && _movable == node._movable;
    }

    private void notifyListeners(Point2D newPosition) {
        for (NodeListener listener : _listeners) {
            listener.onMoved(this, newPosition);
        }
    }
}

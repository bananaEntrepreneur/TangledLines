package model.units;

import model.listeners.NodeChangeListener;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private Point2D _position;
    private final boolean _movable;
    private final List<NodeChangeListener> _listeners = new ArrayList<>();

    public Node(Point2D position, boolean movable) {
        _position = position;
        _movable = movable;
    }

    public void setPosition(Point2D newPosition) {
        if (!_movable || newPosition == null || newPosition.equals(_position)) {
            return;
        }
        _position = newPosition;
        notifyListeners(newPosition);
    }

    public Point2D getPosition() { return _position; }

    public boolean isMovable() { return _movable; }

    public void addListener(NodeChangeListener listener) { _listeners.add(listener); }

    private void notifyListeners(Point2D newPosition) {
        for (NodeChangeListener listener : _listeners) {
            listener.onNodeMoved(this, newPosition);
        }
    }
}

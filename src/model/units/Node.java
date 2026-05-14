package model.units;

import model.listeners.NodeListener;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private Point2D _position;
    private Point2D _queuedPosition = null;
    private boolean _isDragging = false;
    private final List<NodeListener> _listeners = new ArrayList<>();
    private final List<NodeMovementConstraint> _movementConstraints = new ArrayList<>();

    public Node(Point2D position) {
        if (position == null) {
            throw new IllegalArgumentException("Node position cannot be null");
        }
        _position = position;
    }

    public void startDragging() {
        _isDragging = true;
        _queuedPosition = null;
    }

    public void updateDragging(Point2D position) {
        if (!_isDragging || position == null) return;
        _queuedPosition = applyMovementConstraints(position);
    }

    public void stopDragging() {
        if (_isDragging && _queuedPosition != null) {
            setPosition(applyMovementConstraints(_queuedPosition));
        }
        _isDragging = false;
        _queuedPosition = null;
    }

    public Point2D getDragPosition() {
        return _queuedPosition != null ? _queuedPosition : _position;
    }

    public boolean isDragging() { return _isDragging; }

    public Point2D getPosition() { return _position; }

    public void addListener(NodeListener listener) { _listeners.add(listener); }

    public void removeListener(NodeListener listener) { _listeners.remove(listener); }

    public void addMovementConstraint(NodeMovementConstraint constraint) {
        if (constraint != null && !_movementConstraints.contains(constraint)) {
            _movementConstraints.add(constraint);
        }
    }

    private void setPosition(Point2D newPosition) {
        if (newPosition == null || newPosition.equals(_position)) {
            return;
        }
        _position = newPosition;
        notifyListeners();
    }

    private Point2D applyMovementConstraints(Point2D desiredPosition) {
        Point2D position = desiredPosition;
        int guard = 8;

        while (guard-- > 0) {
            boolean changed = false;

            for (NodeMovementConstraint constraint : _movementConstraints) {
                Point2D constrained = constraint.constrain(this, position);
                if (!samePoint(position, constrained)) {
                    position = constrained;
                    changed = true;
                }
            }

            if (!changed) {
                break;
            }
        }

        return position;
    }

    private boolean samePoint(Point2D first, Point2D second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        return Double.compare(first.getX(), second.getX()) == 0
            && Double.compare(first.getY(), second.getY()) == 0;
    }

    private void notifyListeners() {
        for (NodeListener listener : _listeners) {
            listener.onMoved(this);
        }
    }
}

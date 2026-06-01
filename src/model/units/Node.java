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
        _position = copyOf(position);
    }

    public void startDragging() {
        _isDragging = true;
        _queuedPosition = null;
    }

    public void updateDragging(Point2D position) {
        if (!_isDragging || position == null) return;
        _queuedPosition = applyMovementConstraints(position);
        notifyListeners();
    }

    public void stopDragging() {
        Point2D positionToCommit = null;
        if (_isDragging && _queuedPosition != null) {
            positionToCommit = applyMovementConstraints(_queuedPosition);
        }
        _isDragging = false;
        _queuedPosition = null;
        if (positionToCommit != null) {
            setPosition(positionToCommit);
        }
    }

    public Point2D getDragPosition() {
        return copyOf(_queuedPosition != null ? _queuedPosition : _position);
    }

    public boolean isDragging() { return _isDragging; }

    public Point2D getPosition() { return copyOf(_position); }

    public void addListener(NodeListener listener) {
        if (listener != null && !_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

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
        _position = copyOf(newPosition);
        notifyListeners();
    }

    private Point2D applyMovementConstraints(Point2D desiredPosition) {
        Point2D position = copyOf(desiredPosition);
        int guard = 8;

        while (guard-- > 0) {
            boolean changed = false;

            for (NodeMovementConstraint constraint : _movementConstraints) {
                Point2D constrained = constraint.constrain(this, copyOf(position));
                if (constrained == null) {
                    continue;
                }
                if (!samePoint(position, constrained)) {
                    position = copyOf(constrained);
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

    private Point2D copyOf(Point2D point) {
        return new Point2D.Double(point.getX(), point.getY());
    }

    private void notifyListeners() {
        for (NodeListener listener : _listeners) {
            listener.onMoved(this);
        }
    }
}

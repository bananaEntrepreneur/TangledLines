package model.units;

import model.listeners.NodeListener;
import model.game.Field;

import java.awt.geom.Point2D;

public class OverheatingEdge extends Edge implements NodeListener {
    private final double _heatPerIntersection;
    private final double _coolPerMove;
    private final double _criticalHeat;
    private final Field _field;
    private final double _referenceLength;
    private Node _lastMovedNode;
    private Point2D _lastMovePosition;
    private double _heat = 0;
    private boolean _burnedOut = false;

    public OverheatingEdge(
            Node nodeA,
            Node nodeB,
            double heatPerIntersection,
            double coolPerMove,
            double criticalHeat,
            Field field) {
        super(nodeA, nodeB);
        if (heatPerIntersection < 0) {
            throw new IllegalArgumentException("Heat per intersection cannot be negative");
        }
        if (coolPerMove < 0) {
            throw new IllegalArgumentException("Cool per move cannot be negative");
        }
        if (criticalHeat <= 0) {
            throw new IllegalArgumentException("Critical heat must be positive");
        }
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }

        _heatPerIntersection = heatPerIntersection;
        _coolPerMove = coolPerMove;
        _criticalHeat = criticalHeat;
        _field = field;
        _referenceLength = Math.max(1.0, nodeA.getPosition().distance(nodeB.getPosition()));

        nodeA.addListener(this);
        nodeB.addListener(this);
    }

    @Override
    public boolean isActive() {
        return !_burnedOut;
    }

    @Override
    public void onMoved(Node node) {
        if (_burnedOut) {
            return;
        }

        double moveRatio = getMoveRatio(node);
        if (moveRatio <= 0) {
            return;
        }

        if (_field.hasIntersections(this)) {
            _heat += _heatPerIntersection * moveRatio;
        } else {
            _heat = Math.max(0, _heat - _coolPerMove * moveRatio);
        }

        if (_heat >= _criticalHeat) {
            _heat = _criticalHeat;
            _burnedOut = true;
        }
    }

    public double getHeat() {
        return _heat;
    }

    public double getHeatRatio() { return Math.min(1.0, _heat / _criticalHeat); }

    private double getMoveRatio(Node node) {
        Point2D currentPosition = currentPosition(node);
        Point2D previousPosition = node.getPosition();
        if (node == _lastMovedNode && _lastMovePosition != null) {
            previousPosition = _lastMovePosition;
        }

        _lastMovedNode = node;
        _lastMovePosition = currentPosition;
        return previousPosition.distance(currentPosition) / _referenceLength;
    }

    private Point2D currentPosition(Node node) {
        return node.isDragging() ? node.getDragPosition() : node.getPosition();
    }
}

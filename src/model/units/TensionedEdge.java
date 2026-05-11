package model.units;

import java.awt.geom.Point2D;

abstract class TensionedEdge extends Edge {
    private final double _originalLength;

    protected TensionedEdge(Node nodeA, Node nodeB) {
        super(nodeA, nodeB);
        _originalLength = getPosition(nodeA).distance(getPosition(nodeB));
    }

    protected double getOriginalLength() {
        return _originalLength;
    }

    protected double getCurrentLength() {
        return getPosition(getNodeA()).distance(getPosition(getNodeB()));
    }

    protected double getCurrentStretchFactor() {
        if (_originalLength <= 0) {
            return getCurrentLength() > 0 ? Double.POSITIVE_INFINITY : 1.0;
        }
        return getCurrentLength() / _originalLength;
    }

    protected Point2D getPosition(Node node) {
        return node.isDragging() ? node.getDragPosition() : node.getPosition();
    }

    protected Node getOtherNode(Node node) {
        if (node == getNodeA()) {
            return getNodeB();
        }
        if (node == getNodeB()) {
            return getNodeA();
        }
        return null;
    }
}

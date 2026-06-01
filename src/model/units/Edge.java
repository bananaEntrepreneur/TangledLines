package model.units;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public abstract class Edge {
    private final Node _nodeA;
    private final Node _nodeB;

    protected Edge(Node nodeA, Node nodeB) {
        if (nodeA == null || nodeB == null) {
            throw new IllegalArgumentException("Edge endpoints cannot be null");
        }
        _nodeA = nodeA;
        _nodeB = nodeB;
    }

    public boolean crosses(Edge other) {
        if (other == null) return false;
        if (sharesEndpointWith(other)) {
            return false;
        }
        return toLine().intersectsLine(other.toLine());
    }

    public Line2D toLine() {
        Point2D a = positionOf(_nodeA);
        Point2D b = positionOf(_nodeB);
        return new Line2D.Double(a.getX(), a.getY(), b.getX(), b.getY());
    }

    public boolean isActive() {
        return true;
    }

    public Node getNodeA() { return _nodeA; }

    public Node getNodeB() { return _nodeB; }

    private Point2D positionOf(Node node) {
        return node.isDragging() ? node.getDragPosition() : node.getPosition();
    }

    private boolean sharesEndpointWith(Edge other) {
        return _nodeA == other._nodeA || _nodeA == other._nodeB
            || _nodeB == other._nodeA || _nodeB == other._nodeB;
    }
}

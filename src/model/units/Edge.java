package model.units;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Edge {
    private final Node _nodeA;
    private final Node _nodeB;

    public Edge(Node nodeA, Node nodeB) {
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
        Point2D a = _nodeA.getPosition();
        Point2D b = _nodeB.getPosition();
        return new Line2D.Double(a.getX(), a.getY(), b.getX(), b.getY());
    }

    public Node getNodeA() { return _nodeA; }

    public Node getNodeB() { return _nodeB; }

    private boolean sharesEndpointWith(Edge other) {
        return _nodeA == other._nodeA || _nodeA == other._nodeB
            || _nodeB == other._nodeA || _nodeB == other._nodeB;
    }
}

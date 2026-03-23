package model.units;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Edge {
    private final Node _nodeA;
    private final Node _nodeB;

    public Edge(Node nodeA, Node nodeB) {
        _nodeA = nodeA;
        _nodeB = nodeB;
    }

    public Line2D toLine() {
        Point2D a = _nodeA.getPosition();
        Point2D b = _nodeB.getPosition();
        return new Line2D.Double(a.getX(), a.getY(), b.getX(), b.getY());
    }

    public boolean hasIntersection(Edge other) {
        if (sharesNodeWith(other)) {
            return false;
        }
        return this.toLine().intersectsLine(other.toLine());
    }

    private boolean sharesNodeWith(Edge other) {
        return this._nodeA == other._nodeA || this._nodeA == other._nodeB ||
               this._nodeB == other._nodeA || this._nodeB == other._nodeB;
    }
}

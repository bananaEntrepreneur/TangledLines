package model.units;

import java.awt.geom.Line2D;

public class Edge {
    private Node _nodeA;
    private Node _nodeB;

    public Edge(Node nodeA, Node nodeB) {
        _nodeA = nodeA;
        _nodeB = nodeB;
    }

    public boolean hasIntersection(Edge other) {
        if (sharesNodeWith(other)) {
            return false;
        }

        Line2D thisLine = new Line2D.Double(
            _nodeA.getPosition().getX(), _nodeA.getPosition().getY(),
            _nodeB.getPosition().getX(), _nodeB.getPosition().getY()
        );

        Line2D otherLine = new Line2D.Double(
            other._nodeA.getPosition().getX(), other._nodeA.getPosition().getY(),
            other._nodeB.getPosition().getX(), other._nodeB.getPosition().getY()
        );

        return thisLine.intersectsLine(otherLine);
    }

    private boolean sharesNodeWith(Edge other) {
        return this._nodeA == other._nodeA || this._nodeA == other._nodeB ||
               this._nodeB == other._nodeA || this._nodeB == other._nodeB;
    }
}

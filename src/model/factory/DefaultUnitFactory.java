package model.factory;

import model.units.Edge;
import model.units.Node;

import java.awt.geom.Point2D;

public class DefaultUnitFactory implements UnitFactory {
    @Override
    public Node createNode(Point2D position, boolean movable) {
        return new Node(position, movable);
    }

    @Override
    public Edge createEdge(Node nodeA, Node nodeB) {
        return new Edge(nodeA, nodeB);
    }
}

package model.factory;

import model.units.Edge;
import model.units.Node;

import java.awt.geom.Point2D;

public interface UnitFactory {
    Node createNode(Point2D position, boolean movable);
    Edge createEdge(Node nodeA, Node nodeB);
}

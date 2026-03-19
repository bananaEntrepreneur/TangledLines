package model.game;

import model.units.Edge;
import model.units.Node;

import java.awt.geom.Point2D;
import java.util.List;

public class Field {
    private List<Node> _nodes;
    private List<Edge> _edges;

    public boolean moveNodeTo(Node node, Point2D newPosition) {
        return true;
    }
}

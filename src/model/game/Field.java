package model.game;

import model.factory.UnitFactory;
import model.units.Edge;
import model.units.Node;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Field {
    private static final double MIN_MOVE_DISTANCE_SQUARED = 1.0;
    private final UnitFactory _factory;
    private final List<Node> _nodes = new ArrayList<>();
    private final List<Edge> _edges = new ArrayList<>();

    public Field(UnitFactory factory) {
        _factory = factory;
    }

    public void createNode(Point2D position, boolean movable) {
        Node node = _factory.createNode(position, movable);
        addNode(node);
    }

    public void createEdge(Node nodeA, Node nodeB) {
        Edge edge = _factory.createEdge(nodeA, nodeB);
        addEdge(edge);
    }

    public boolean moveNode(Node node, Point2D newPosition) {
        if (!isValidMove(node, newPosition)) {
            return false;
        }
        node.setPosition(newPosition);
        return true;
    }

    public List<Node> getNodes() { return List.copyOf(_nodes); }

    public List<Edge> getEdges() { return List.copyOf(_edges); }

    private void addNode(Node node) {
        if (!_nodes.contains(node)) {
            _nodes.add(node);
        }
    }

    private void addEdge(Edge edge) {
        if (edge == null) {
            return;
        }
        addNode(edge.getNodeA());
        addNode(edge.getNodeB());
        if (!_edges.contains(edge)) {
            _edges.add(edge);
        }
    }

    private boolean isValidMove(Node node, Point2D newPosition) {
        if (node == null || newPosition == null || !_nodes.contains(node) || !node.isMovable()) {
            return false;
        }
        return hasMovedSignificantly(node.getPosition(), newPosition);
    }

    private boolean hasMovedSignificantly(Point2D oldPos, Point2D newPos) {
        double dx = oldPos.getX() - newPos.getX();
        double dy = oldPos.getY() - newPos.getY();
        return dx * dx + dy * dy >= MIN_MOVE_DISTANCE_SQUARED;
    }
}

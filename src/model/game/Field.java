package model.game;

import model.factory.UnitFactory;
import model.listeners.TransactionListener;
import model.units.Edge;
import model.units.Node;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Field implements TransactionListener{
    private final UnitFactory _factory;
    private final List<Node> _nodes = new ArrayList<>();
    private final List<Edge> _edges = new ArrayList<>();

    public Field(UnitFactory factory) {
        _factory = factory;
    }

    @Override
    public void onCommitted(Node node, Point2D finalPosition) {
        if (!finalPosition.equals(node.getPosition())) {
            moveNode(node, finalPosition);
        }
    }

    public void createNode(Point2D position, boolean movable) {
        Node node = _factory.createNode(position, movable);
        addNode(node);
    }

    public void createEdge(Node nodeA, Node nodeB) {
        Edge edge = _factory.createEdge(nodeA, nodeB);
        addEdge(edge);
    }

    public boolean hasIntersections() {
        int size = _edges.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (_edges.get(i).crosses(_edges.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Node> getNodes() { return List.copyOf(_nodes); }
    public List<Edge> getEdges() { return List.copyOf(_edges); }

    private boolean moveNode(Node node, Point2D newPosition) {
        if (node == null || !_nodes.contains(node)) {
            return false;
        }
        return node.move(newPosition);
    }

    private void addNode(Node node) {
        if (!_nodes.contains(node)) {
            _nodes.add(node);
        }
    }

    private void addEdge(Edge edge) {
        if (edge == null) return;
        addNode(edge.getNodeA());
        addNode(edge.getNodeB());
        if (!_edges.contains(edge)) {
            _edges.add(edge);
        }
    }
}
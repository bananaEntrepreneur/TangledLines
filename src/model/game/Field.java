package model.game;

import model.units.Edge;
import model.units.Node;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Field {
    private final List<Node> _nodes = new ArrayList<>();
    private final List<Edge> _edges = new ArrayList<>();

    public void addNode(Node node) {
        if (node != null && !_nodes.contains(node))
            _nodes.add(node);
    }

    public void addEdge(Edge edge) {
        if (edge == null)
                return;

        addNode(edge.getNodeA());
        addNode(edge.getNodeB());

        if (!_edges.contains(edge))
            _edges.add(edge);
    }

    public boolean moveNode(Node node, Point2D newPosition) {
        if (node == null || newPosition == null || !_nodes.contains(node)) {
            return false;
        }
        Point2D oldPos = node.getPosition();
        node.setPosition(newPosition);
        return !oldPos.equals(node.getPosition());
    }

    public boolean hasIntersections() {
        for (int i = 0; i < _edges.size(); i++) {
            for (int j = i + 1; j < _edges.size(); j++) {
                if (_edges.get(i).hasIntersection(_edges.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Node> getNodes() { return List.copyOf(_nodes); }

    public List<Edge> getEdges() { return List.copyOf(_edges); }
}

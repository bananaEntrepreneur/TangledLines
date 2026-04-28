package model.game;

import model.units.Edge;
import model.units.Node;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Field {
    private final List<Node> _nodes = new ArrayList<>();
    private final List<Edge> _edges = new ArrayList<>();

    public void createNode(Point2D position) {
        Node node = new Node(position);
        addNode(node);
    }

    public void createEdge(Node nodeA, Node nodeB) {
        Edge edge = new Edge(nodeA, nodeB);

        addNode(edge.getNodeA());
        addNode(edge.getNodeB());

        if (!_edges.contains(edge)) {
            _edges.add(edge);
        }
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

    private void addNode(Node node) {
        if (!_nodes.contains(node)) {
            _nodes.add(node);
        }
    }
}
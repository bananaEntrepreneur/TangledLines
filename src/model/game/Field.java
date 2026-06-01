package model.game;

import model.units.Edge;
import model.units.StandardEdge;
import model.units.BreakableEdge;
import model.units.StretchableEdge;
import model.units.OverheatingEdge;
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

    public Edge createEdge(Node nodeA, Node nodeB) {
        return addEdge(new StandardEdge(nodeA, nodeB));
    }

    public StretchableEdge createStretchableEdge(Node nodeA, Node nodeB, double stretchPercent) {
        return (StretchableEdge) addEdge(new StretchableEdge(nodeA, nodeB, stretchPercent));
    }

    public BreakableEdge createBreakableEdge(Node nodeA, Node nodeB, double breakPercent) {
        return (BreakableEdge) addEdge(new BreakableEdge(nodeA, nodeB, breakPercent));
    }

    public OverheatingEdge createOverheatingEdge(
            Node nodeA,
            Node nodeB,
            double heatPerIntersection,
            double coolPerMove,
            double criticalHeat) {
        return (OverheatingEdge) addEdge(
            new OverheatingEdge(nodeA, nodeB, heatPerIntersection, coolPerMove, criticalHeat, this)
        );
    }

    public boolean hasIntersections() {
        int size = _edges.size();
        for (int i = 0; i < size; i++) {
            if (!_edges.get(i).isActive()) {
                continue;
            }
            for (int j = i + 1; j < size; j++) {
                if (!_edges.get(j).isActive()) {
                    continue;
                }
                if (_edges.get(i).crosses(_edges.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasIntersections(Edge target) {
        if (target == null || !target.isActive()) {
            return false;
        }

        for (Edge edge : _edges) {
            if (edge != target && edge.isActive() && target.crosses(edge)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasInactiveEdges() {
        for (Edge edge : _edges) {
            if (!edge.isActive()) {
                return true;
            }
        }
        return false;
    }

    public List<Node> getNodes() { return List.copyOf(_nodes); }
    public List<Edge> getEdges() { return List.copyOf(_edges); }

    public Edge addEdge(Edge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge cannot be null");
        }

        addNode(edge.getNodeA());
        addNode(edge.getNodeB());

        if (!_edges.contains(edge)) {
            _edges.add(edge);
        }
        if (edge instanceof OverheatingEdge) {
            addListenerToAllNodes((OverheatingEdge) edge);
        }
        return edge;
    }

    private void addNode(Node node) {
        if (!_nodes.contains(node)) {
            _nodes.add(node);
            addExistingOverheatingEdgesTo(node);
        }
    }

    private void addExistingOverheatingEdgesTo(Node node) {
        for (Edge edge : _edges) {
            if (edge instanceof OverheatingEdge) {
                node.addListener((OverheatingEdge) edge);
            }
        }
    }

    private void addListenerToAllNodes(OverheatingEdge edge) {
        for (Node node : _nodes) {
            node.addListener(edge);
        }
    }
}

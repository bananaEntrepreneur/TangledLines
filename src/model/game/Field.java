package model.game;

import model.listeners.NodeChangeListener;
import model.units.Edge;
import model.units.Node;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Field {
    private List<Node> _nodes;
    private List<Edge> _edges;

    public Field() {
        _nodes = new ArrayList<>();
        _edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        _nodes.add(node);
        node.addListener((NodeChangeListener) this);
    }

    public void addEdge(Edge edge) {
        _edges.add(edge);
    }

    public List<Node> getNodes() {
        return new ArrayList<>(_nodes);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(_edges);
    }

    public boolean moveNodeTo(Node node, Point2D newPosition) {
        if (node == null || newPosition == null || !_nodes.contains(node)) {
            return false;
        }

        node.setPosition(newPosition);
        return true;
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
}

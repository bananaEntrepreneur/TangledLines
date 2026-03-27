package model.level;

import java.util.List;

public class Level {
    private final int _maxMoves;
    private final List<NodeData> _nodes;
    private final List<EdgeData> _edges;

    public Level(int maxMoves, List<NodeData> nodes, List<EdgeData> edges) {
        if (maxMoves < 1) {
            throw new IllegalArgumentException();
        }
        _maxMoves = maxMoves;
        if (nodes == null) {
            throw new IllegalArgumentException();
        }
        _nodes = nodes;
        if (edges == null) {
            throw new IllegalArgumentException();
        }
        _edges = edges;
    }

    public int getMaxMoves() { return _maxMoves; }
    public List<NodeData> getNodes() { return _nodes; }
    public List<EdgeData> getEdges() { return _edges; }

    public record NodeData(double x, double y, boolean movable) {}
    public record EdgeData(int nodeAIndex, int nodeBIndex) {}
}

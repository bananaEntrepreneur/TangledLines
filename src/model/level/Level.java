package model.level;

import java.util.List;

public class Level {
    private final int _maxMoves;
    private final List<NodeData> _nodes;
    private final List<EdgeData> _edges;

    public Level(int maxMoves, List<NodeData> nodes, List<EdgeData> edges) {
        if (maxMoves < 1) {
            throw new IllegalArgumentException("maxMoves must be at least 1, got: " + maxMoves);
        }
        _maxMoves = maxMoves;
        
        if (nodes == null) {
            throw new IllegalArgumentException("nodes list cannot be null");
        }
        _nodes = nodes;
        
        if (edges == null) {
            throw new IllegalArgumentException("edges list cannot be null");
        }
        _edges = edges;
    }

    public int getMaxMoves() { return _maxMoves; }
    public List<NodeData> getNodes() { return _nodes; }
    public List<EdgeData> getEdges() { return _edges; }

    public record NodeData(double x, double y) {}
    public record EdgeData(int nodeAIndex, int nodeBIndex) {}
}

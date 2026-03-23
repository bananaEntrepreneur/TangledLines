package model.game;

import model.units.Edge;

import java.util.List;

public interface IntersectionChecker {
    boolean hasIntersections(List<Edge> edges);
}

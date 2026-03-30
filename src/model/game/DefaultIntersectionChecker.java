package model.game;

import model.units.Edge;

import java.util.List;

public class DefaultIntersectionChecker implements IntersectionChecker {
    @Override
    public boolean hasIntersections(List<Edge> edges) {
        int size = edges.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (edges.get(i).intersects(edges.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }
}

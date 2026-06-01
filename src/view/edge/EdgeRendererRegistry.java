package view.edge;

import model.units.Edge;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public final class EdgeRendererRegistry {
    private final List<EdgeRenderer> _renderers = new ArrayList<>();

    public static EdgeRendererRegistry withDefaults() {
        return new EdgeRendererRegistry()
            .register(new BasicEdgeRenderer())
            .register(new StretchableEdgeRenderer())
            .register(new BreakableEdgeRenderer())
            .register(new OverheatingEdgeRenderer());
    }

    public EdgeRendererRegistry register(EdgeRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Edge renderer cannot be null");
        }
        _renderers.add(renderer);
        return this;
    }

    public void draw(Graphics2D g2d, Edge edge, Point2D start, Point2D end) {
        rendererFor(edge).draw(g2d, edge, start, end);
    }

    private EdgeRenderer rendererFor(Edge edge) {
        for (int i = _renderers.size() - 1; i >= 0; i--) {
            EdgeRenderer renderer = _renderers.get(i);
            if (renderer.supports(edge)) {
                return renderer;
            }
        }
        throw new IllegalStateException("No renderer registered for edge: " + edge.getClass().getName());
    }
}

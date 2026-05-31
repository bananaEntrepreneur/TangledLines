package unit;

import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import view.edge.EdgeRenderer;
import view.edge.EdgeRendererRegistry;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Edge Renderer Registry Tests")
class EdgeRendererRegistryTest {

    @Test
    @DisplayName("Should use registered renderer for custom edge")
    void shouldUseRegisteredRendererForCustomEdge() {
        RecordingRenderer renderer = new RecordingRenderer();
        EdgeRendererRegistry registry = EdgeRendererRegistry.withDefaults()
            .register(renderer);

        Edge edge = new PaintedEdge(
            new Node(new Point2D.Double(0, 0)),
            new Node(new Point2D.Double(10, 10))
        );

        BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        registry.draw(g2d, edge, edge.getNodeA().getPosition(), edge.getNodeB().getPosition());
        g2d.dispose();

        assertTrue(renderer.wasCalled);
    }

    private static class RecordingRenderer implements EdgeRenderer {
        private boolean wasCalled = false;

        @Override
        public boolean supports(Edge edge) {
            return edge instanceof PaintedEdge;
        }

        @Override
        public void draw(Graphics2D g2d, Edge edge, Point2D start, Point2D end) {
            wasCalled = true;
        }
    }

    private static class PaintedEdge extends Edge {
        PaintedEdge(Node nodeA, Node nodeB) {
            super(nodeA, nodeB);
        }
    }
}

package unit;

import model.units.Node;
import model.units.StretchableEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Stretchable Edge Tests")
class StretchableEdgeTest {

    @Test
    @DisplayName("Should clamp dragged node to max stretch")
    void shouldClampDraggedNodeToMaxStretch() {
        Node nodeA = new Node(new Point2D.Double(0, 0));
        Node nodeB = new Node(new Point2D.Double(100, 0));

        StretchableEdge edge = new StretchableEdge(nodeA, nodeB, 50);

        nodeB.startDragging();
        nodeB.updateDragging(new Point2D.Double(220, 0));

        assertEquals(150.0, nodeB.getDragPosition().getX(), 0.01);

        nodeB.stopDragging();

        assertEquals(150.0, nodeB.getPosition().getX(), 0.01);
        assertEquals(100.0, edge.getOriginalLength(), 0.01);
        assertEquals(150.0, edge.getMaxLength(), 0.01);
    }

    @Test
    @DisplayName("Should report near limit state")
    void shouldReportNearLimitState() {
        Node nodeA = new Node(new Point2D.Double(0, 0));
        Node nodeB = new Node(new Point2D.Double(100, 0));

        StretchableEdge edge = new StretchableEdge(nodeA, nodeB, 25);

        nodeB.startDragging();
        nodeB.updateDragging(new Point2D.Double(110, 0));

        assertEquals(110.0, nodeB.getDragPosition().getX(), 0.01);
        assertEquals(1.10, edge.getStretchFactor(), 0.01);
        assertFalse(edge.isNearLimit());

        nodeB.updateDragging(new Point2D.Double(123, 0));

        assertEquals(123.0, nodeB.getDragPosition().getX(), 0.01);
        assertTrue(edge.isNearLimit());
    }
}

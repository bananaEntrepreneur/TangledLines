package unit;

import model.units.BreakableEdge;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Breakable Edge Tests")
class BreakableEdgeTest {

    @Test
    @DisplayName("Should become ready to break before failing")
    void shouldBecomeReadyToBreakBeforeFailing() {
        Node nodeA = new Node(new Point2D.Double(0, 0));
        Node nodeB = new Node(new Point2D.Double(100, 0));

        BreakableEdge edge = new BreakableEdge(nodeA, nodeB, 50);

        nodeB.startDragging();
        nodeB.updateDragging(new Point2D.Double(140, 0));

        assertTrue(edge.isActive());
        assertTrue(edge.isReadyToBreak());
        assertEquals(140.0, edge.getCurrentLength(), 0.01);
    }

    @Test
    @DisplayName("Should break when stretched beyond threshold")
    void shouldBreakWhenStretchedBeyondThreshold() {
        Node nodeA = new Node(new Point2D.Double(0, 0));
        Node nodeB = new Node(new Point2D.Double(100, 0));

        BreakableEdge edge = new BreakableEdge(nodeA, nodeB, 20);

        nodeB.startDragging();
        nodeB.updateDragging(new Point2D.Double(140, 0));
        nodeB.stopDragging();

        assertFalse(edge.isActive());
        assertEquals(140.0, nodeB.getPosition().getX(), 0.01);
    }
}

package unit;

import model.game.Field;
import model.units.Node;
import model.units.OverheatingEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Overheating Edge Tests")
class OverheatingEdgeTest {

    @Test
    @DisplayName("Should heat while intersecting and cool when clear")
    void shouldHeatAndCool() {
        Field field = new Field();
        Node nodeA = node(0, 0);
        Node nodeB = node(100, 0);
        Node nodeC = node(50, -50);
        Node nodeD = node(50, 50);

        OverheatingEdge edge = new OverheatingEdge(nodeA, nodeB, 30, 10, 100, field);
        field.addEdge(edge);
        field.createEdge(nodeC, nodeD);

        drag(nodeA, 10, 0);
        drag(nodeA, 20, 0);

        nodeC.startDragging();
        nodeC.updateDragging(new Point2D.Double(200, -50));
        edge.onMoved(nodeC);
        nodeC.stopDragging();

        assertTrue(edge.isActive());
        assertEquals(0.0, edge.getHeat(), 0.01);
        assertEquals(0.0, edge.getHeatRatio(), 0.01);
    }

    @Test
    @DisplayName("Should heat slowly for small drag updates")
    void shouldHeatSlowlyForSmallDragUpdates() {
        Field field = new Field();
        Node nodeA = node(0, 0);
        Node nodeB = node(100, 0);

        OverheatingEdge edge = new OverheatingEdge(nodeA, nodeB, 30, 10, 100, field);
        field.addEdge(edge);
        field.createEdge(node(50, -50), node(50, 50));

        drag(nodeA, 1, 0);
        drag(nodeA, 2, 0);
        drag(nodeA, 3, 0);
        drag(nodeA, 4, 0);

        assertTrue(edge.isActive());
        assertEquals(1.2, edge.getHeat(), 0.01);
    }

    @Test
    @DisplayName("Should become inactive when overheated")
    void shouldBecomeInactiveWhenOverheated() {
        Field field = new Field();
        Node nodeA = node(0, 0);
        OverheatingEdge edge = new OverheatingEdge(nodeA, node(100, 0), 60, 10, 100, field);
        field.addEdge(edge);
        field.createEdge(node(50, -50), node(50, 50));

        drag(nodeA, -100, 0);
        drag(nodeA, -200, 0);

        assertFalse(edge.isActive());
        assertEquals(1.0, edge.getHeatRatio(), 0.01);
    }

    private Node node(double x, double y) {
        return new Node(new Point2D.Double(x, y));
    }

    private void drag(Node node, double x, double y) {
        node.startDragging();
        node.updateDragging(new Point2D.Double(x, y));
        node.stopDragging();
    }
}

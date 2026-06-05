package unit;

import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class EdgeBehaviorContract {
    protected abstract Edge createEdge(Node nodeA, Node nodeB);

    @Test
    @DisplayName("Should keep endpoint references")
    void shouldKeepEndpointReferences() {
        Node nodeA = makeNode(0, 0);
        Node nodeB = makeNode(100, 100);

        Edge edge = createEdge(nodeA, nodeB);

        assertSame(nodeA, edge.getNodeA());
        assertSame(nodeB, edge.getNodeB());
    }

    @Test
    @DisplayName("Should be active after creation")
    void shouldBeActiveAfterCreation() {
        Edge edge = createEdge(makeNode(0, 0), makeNode(100, 100));

        assertTrue(edge.isActive());
    }

    @Test
    @DisplayName("Should expose line from current endpoint positions")
    void shouldExposeLineFromCurrentEndpointPositions() {
        Node nodeA = makeNode(0, 0);
        Node nodeB = makeNode(100, 100);
        Edge edge = createEdge(nodeA, nodeB);

        drag(nodeA, 50, 50);
        drag(nodeB, 150, 150);

        Line2D line = edge.toLine();

        assertEquals(50.0, line.getX1(), 0.01);
        assertEquals(50.0, line.getY1(), 0.01);
        assertEquals(150.0, line.getX2(), 0.01);
        assertEquals(150.0, line.getY2(), 0.01);
    }

    @Test
    @DisplayName("Should detect crossing edges")
    void shouldDetectCrossingEdges() {
        Edge edge = createEdge(makeNode(0, 0), makeNode(100, 100));
        Edge crossingEdge = createEdge(makeNode(0, 100), makeNode(100, 0));

        assertTrue(edge.crosses(crossingEdge));
        assertTrue(crossingEdge.crosses(edge));
    }

    @Test
    @DisplayName("Should ignore edges sharing endpoint")
    void shouldIgnoreEdgesSharingEndpoint() {
        Node sharedNode = makeNode(50, 50);
        Edge edge = createEdge(makeNode(0, 0), sharedNode);
        Edge adjacentEdge = createEdge(sharedNode, makeNode(100, 0));

        assertFalse(edge.crosses(adjacentEdge));
        assertFalse(adjacentEdge.crosses(edge));
    }

    private Node makeNode(double x, double y) {
        return new Node(new Point2D.Double(x, y));
    }

    private void drag(Node node, double x, double y) {
        node.startDragging();
        node.updateDragging(new Point2D.Double(x, y));
        node.stopDragging();
    }
}

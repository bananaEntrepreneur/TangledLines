package unit;

import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class EdgeBehaviorContract {
    protected abstract Edge createEdge(Node nodeA, Node nodeB);

    @Test
    @DisplayName("LSP precondition: Should reject null endpoint nodes")
    void lspPreconditionShouldRejectNullEndpointNodes() {
        Node node = makeNode(0, 0);

        assertThrows(IllegalArgumentException.class, () -> createEdge(null, node));
        assertThrows(IllegalArgumentException.class, () -> createEdge(node, null));
        assertThrows(IllegalArgumentException.class, () -> createEdge(null, null));
    }

    @Test
    @DisplayName("LSP precondition: Should accept null in crosses as non-crossing edge")
    void lspPreconditionShouldAcceptNullInCrossesAsNonCrossingEdge() {
        Node nodeA = makeNode(0, 0);
        Node nodeB = makeNode(100, 100);
        Edge edge = createEdge(nodeA, nodeB);

        assertFalse(edge.crosses(null));

        assertSame(nodeA, edge.getNodeA());
        assertSame(nodeB, edge.getNodeB());
        assertEquals(new Point2D.Double(0, 0), nodeA.getPosition());
        assertEquals(new Point2D.Double(100, 100), nodeB.getPosition());
    }

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
    @DisplayName("LSP invariant: Should keep endpoint identity after movement and queries")
    void lspInvariantShouldKeepEndpointIdentityAfterMovementAndQueries() {
        Node nodeA = makeNode(0, 0);
        Node nodeB = makeNode(100, 100);
        Edge edge = createEdge(nodeA, nodeB);
        Edge other = createEdge(makeNode(0, 100), makeNode(100, 0));

        drag(nodeA, 10, 20);
        edge.toLine();
        edge.crosses(other);
        edge.isActive();

        assertSame(nodeA, edge.getNodeA());
        assertSame(nodeB, edge.getNodeB());
        assertEquals(new Point2D.Double(10, 20), nodeA.getPosition());
        assertEquals(new Point2D.Double(100, 100), nodeB.getPosition());
    }

    @Test
    @DisplayName("Should be active after creation")
    void shouldBeActiveAfterCreation() {
        Edge edge = createEdge(makeNode(0, 0), makeNode(100, 100));

        assertTrue(edge.isActive());
    }

    @Test
    @DisplayName("LSP invariant: Should expose preview endpoint positions before commit")
    void lspInvariantShouldExposePreviewEndpointPositionsBeforeCommit() {
        Node nodeA = makeNode(0, 0);
        Node nodeB = makeNode(100, 100);
        Edge edge = createEdge(nodeA, nodeB);

        nodeA.startDragging();
        nodeA.updateDragging(new Point2D.Double(25, 25));

        Line2D line = edge.toLine();

        assertEquals(25.0, line.getX1(), 0.01);
        assertEquals(25.0, line.getY1(), 0.01);
        assertEquals(100.0, line.getX2(), 0.01);
        assertEquals(100.0, line.getY2(), 0.01);
        assertEquals(new Point2D.Double(0, 0), nodeA.getPosition());
        assertEquals(new Point2D.Double(25, 25), nodeA.getDragPosition());
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
    @DisplayName("LSP postcondition: toLine should return a snapshot, not mutable model state")
    void lspPostconditionToLineShouldReturnSnapshotNotMutableModelState() {
        Node nodeA = makeNode(0, 0);
        Node nodeB = makeNode(100, 100);
        Edge edge = createEdge(nodeA, nodeB);

        Line2D line = edge.toLine();
        line.setLine(500, 500, 600, 600);
        Line2D nextLine = edge.toLine();

        assertNotSame(line, nextLine);
        assertEquals(0.0, nextLine.getX1(), 0.01);
        assertEquals(0.0, nextLine.getY1(), 0.01);
        assertEquals(100.0, nextLine.getX2(), 0.01);
        assertEquals(100.0, nextLine.getY2(), 0.01);
        assertEquals(new Point2D.Double(0, 0), nodeA.getPosition());
        assertEquals(new Point2D.Double(100, 100), nodeB.getPosition());
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
    @DisplayName("LSP postcondition: crosses should be symmetric and side-effect free")
    void lspPostconditionCrossesShouldBeSymmetricAndSideEffectFree() {
        Node nodeA = makeNode(0, 0);
        Node nodeB = makeNode(100, 100);
        Node nodeC = makeNode(0, 100);
        Node nodeD = makeNode(100, 0);
        Edge edge = createEdge(nodeA, nodeB);
        Edge crossingEdge = createEdge(nodeC, nodeD);

        boolean edgeCrosses = edge.crosses(crossingEdge);
        boolean crossingEdgeCrosses = crossingEdge.crosses(edge);

        assertTrue(edgeCrosses);
        assertTrue(crossingEdgeCrosses);
        assertSame(nodeA, edge.getNodeA());
        assertSame(nodeB, edge.getNodeB());
        assertSame(nodeC, crossingEdge.getNodeA());
        assertSame(nodeD, crossingEdge.getNodeB());
        assertEquals(new Point2D.Double(0, 0), nodeA.getPosition());
        assertEquals(new Point2D.Double(100, 100), nodeB.getPosition());
        assertEquals(new Point2D.Double(0, 100), nodeC.getPosition());
        assertEquals(new Point2D.Double(100, 0), nodeD.getPosition());
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

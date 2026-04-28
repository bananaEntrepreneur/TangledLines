package unit;

import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Edge Unit Tests")
class EdgeTest {

    private Point2D _point1;
    private Point2D _point2;
    private Point2D _point3;
    private Point2D _point4;

    @BeforeEach
    void setUp() {
        _point1 = new Point2D.Double(0, 0);
        _point2 = new Point2D.Double(100, 100);
        _point3 = new Point2D.Double(0, 100);
        _point4 = new Point2D.Double(100, 0);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create edge with two nodes")
        void shouldCreateEdgeWithTwoNodes() {
            Node nodeA = new Node(_point1);
            Node nodeB = new Node(_point2);

            Edge edge = new Edge(nodeA, nodeB);

            assertNotNull(edge);
            assertEquals(nodeA, edge.getNodeA());
            assertEquals(nodeB, edge.getNodeB());
        }

        @Test
        @DisplayName("Should store node references correctly")
        void shouldStoreNodeReferences() {
            Node nodeA = new Node(_point1);
            Node nodeB = new Node(_point2);

            Edge edge = new Edge(nodeA, nodeB);

            assertSame(nodeA, edge.getNodeA());
            assertSame(nodeB, edge.getNodeB());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct node A")
        void shouldReturnCorrectNodeA() {
            Node nodeA = new Node(_point1);
            Node nodeB = new Node(_point2);
            Edge edge = new Edge(nodeA, nodeB);

            assertEquals(nodeA, edge.getNodeA());
        }

        @Test
        @DisplayName("Should return correct node B")
        void shouldReturnCorrectNodeB() {
            Node nodeA = new Node(_point1);
            Node nodeB = new Node(_point2);
            Edge edge = new Edge(nodeA, nodeB);

            assertEquals(nodeB, edge.getNodeB());
        }
    }

    @Nested
    @DisplayName("toLine Tests")
    class ToLineTests {

        @Test
        @DisplayName("Should create Line2D from node positions")
        void shouldCreateLineFromNodePositions() {
            Node nodeA = new Node(_point1);
            Node nodeB = new Node(_point2);
            Edge edge = new Edge(nodeA, nodeB);

            Line2D line = edge.toLine();

            assertNotNull(line);
            assertEquals(_point1.getX(), line.getX1());
            assertEquals(_point1.getY(), line.getY1());
            assertEquals(_point2.getX(), line.getX2());
            assertEquals(_point2.getY(), line.getY2());
        }

        @Test
        @DisplayName("Should reflect updated node positions")
        void shouldReflectUpdatedNodePositions() {
            Node nodeA = new Node(_point1);
            Node nodeB = new Node(_point2);
            Edge edge = new Edge(nodeA, nodeB);

            Point2D newPositionA = new Point2D.Double(50, 50);
            Point2D newPositionB = new Point2D.Double(150, 150);
            nodeA.move(newPositionA);
            nodeB.move(newPositionB);

            Line2D line = edge.toLine();

            assertEquals(newPositionA.getX(), line.getX1());
            assertEquals(newPositionA.getY(), line.getY1());
            assertEquals(newPositionB.getX(), line.getX2());
            assertEquals(newPositionB.getY(), line.getY2());
        }
    }

    @Nested
    @DisplayName("intersects Tests - Intersecting Edges")
    class IntersectsIntersectingTests {

        @Test
        @DisplayName("Should detect intersection between crossing edges")
        void shouldDetectCrossingEdges() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0));
            Node nodeB1 = new Node(new Point2D.Double(100, 100));
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(0, 100));
            Node nodeB2 = new Node(new Point2D.Double(100, 0));
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertTrue(edge1.crosses(edge2));
            assertTrue(edge2.crosses(edge1));
        }

        @Test
        @DisplayName("Should detect intersection for perpendicular edges")
        void shouldDetectPerpendicularIntersection() {
            Node nodeA1 = new Node(new Point2D.Double(50, 0));
            Node nodeB1 = new Node(new Point2D.Double(50, 100));
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(0, 50));
            Node nodeB2 = new Node(new Point2D.Double(100, 50));
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertTrue(edge1.crosses(edge2));
        }

        @Test
        @DisplayName("Should detect intersection for T-junction")
        void shouldDetectTJunction() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0));
            Node nodeB1 = new Node(new Point2D.Double(100, 0));
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(50, 0));
            Node nodeB2 = new Node(new Point2D.Double(50, 100));
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertTrue(edge1.crosses(edge2));
        }
    }

    @Nested
    @DisplayName("intersects Tests - Non-Intersecting Edges")
    class IntersectsNonIntersectingTests {

        @Test
        @DisplayName("Should return false for parallel non-overlapping edges")
        void shouldReturnFalseForParallelEdges() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0));
            Node nodeB1 = new Node(new Point2D.Double(100, 0));
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(0, 50));
            Node nodeB2 = new Node(new Point2D.Double(100, 50));
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertFalse(edge1.crosses(edge2));
        }

        @Test
        @DisplayName("Should return false for edges far apart")
        void shouldReturnFalseForEdgesFarApart() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0));
            Node nodeB1 = new Node(new Point2D.Double(10, 10));
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(100, 100));
            Node nodeB2 = new Node(new Point2D.Double(110, 110));
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertFalse(edge1.crosses(edge2));
        }

        @Test
        @DisplayName("Should return false for adjacent edges sharing a node")
        void shouldReturnFalseForAdjacentEdges() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0));
            Node sharedNode = new Node(new Point2D.Double(50, 0));
            Edge edge1 = new Edge(nodeA1, sharedNode);

            Node nodeB2 = new Node(new Point2D.Double(100, 0));
            Edge edge2 = new Edge(sharedNode, nodeB2);

            assertFalse(edge1.crosses(edge2));
        }
    }

    @Nested
    @DisplayName("intersects Tests - Shared Nodes")
    class IntersectsSharedNodesTests {

        @Test
        @DisplayName("Should return false when edges share node A")
        void shouldReturnFalseWhenSharingNodeA() {
            Node sharedNode = new Node(new Point2D.Double(0, 0));
            Node nodeB1 = new Node(new Point2D.Double(100, 100));
            Edge edge1 = new Edge(sharedNode, nodeB1);

            Node nodeB2 = new Node(new Point2D.Double(0, 100));
            Edge edge2 = new Edge(sharedNode, nodeB2);

            assertFalse(edge1.crosses(edge2));
        }

        @Test
        @DisplayName("Should return false when edges share node B")
        void shouldReturnFalseWhenSharingNodeB() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0));
            Node sharedNode = new Node(new Point2D.Double(100, 100));
            Edge edge1 = new Edge(nodeA1, sharedNode);

            Node nodeA2 = new Node(new Point2D.Double(0, 100));
            Edge edge2 = new Edge(nodeA2, sharedNode);

            assertFalse(edge1.crosses(edge2));
        }

        @Test
        @DisplayName("Should return false when edges share both nodes (same edge)")
        void shouldReturnFalseWhenSharingBothNodes() {
            Node nodeA = new Node(new Point2D.Double(0, 0));
            Node nodeB = new Node(new Point2D.Double(100, 100));
            Edge edge1 = new Edge(nodeA, nodeB);
            Edge edge2 = new Edge(nodeA, nodeB);

            assertFalse(edge1.crosses(edge2));
            assertFalse(edge2.crosses(edge1));
        }

        @Test
        @DisplayName("Should return false when edges cross-share a node")
        void shouldReturnFalseWhenCrossSharingNodes() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0));
            Node sharedNode = new Node(new Point2D.Double(100, 100));
            Edge edge1 = new Edge(nodeA1, sharedNode);

            Node nodeB2 = new Node(new Point2D.Double(0, 100));
            Edge edge2 = new Edge(sharedNode, nodeB2);

            assertFalse(edge1.crosses(edge2));
        }
    }

    @Nested
    @DisplayName("intersects Tests - Edge Cases")
    class IntersectsEdgeCases {

        @Test
        @DisplayName("Should return false for zero-length edge (same node reference)")
        void shouldHandleZeroLengthEdge() {
            Node nodeA = new Node(new Point2D.Double(0, 0));
            Node nodeB = new Node(new Point2D.Double(100, 100));
            Node isolatedNode = new Node(new Point2D.Double(200, 200));

            Edge zeroLengthEdge = new Edge(isolatedNode, isolatedNode);
            Edge normalEdge = new Edge(nodeA, nodeB);

            assertFalse(zeroLengthEdge.crosses(normalEdge));
        }

        @Test
        @DisplayName("Should handle overlapping collinear edges")
        void shouldHandleOverlappingCollinearEdges() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0));
            Node nodeB1 = new Node(new Point2D.Double(100, 0));
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(25, 0));
            Node nodeB2 = new Node(new Point2D.Double(75, 0));
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertTrue(edge1.crosses(edge2));
        }

        @Test
        @DisplayName("Should return false for edges touching at shared endpoint")
        void shouldHandleEdgesTouchingAtEndpoints() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0));
            Node sharedNode = new Node(new Point2D.Double(50, 50));
            Edge edge1 = new Edge(nodeA1, sharedNode);

            Node nodeB2 = new Node(new Point2D.Double(100, 0));
            Edge edge2 = new Edge(sharedNode, nodeB2);

            assertFalse(edge1.crosses(edge2));
        }
    }
}

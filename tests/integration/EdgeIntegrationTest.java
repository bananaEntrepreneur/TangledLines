package integration;

import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Edge Integration Tests")
class EdgeIntegrationTest {

    @Nested
    @DisplayName("Dynamic Node Movement Tests")
    class DynamicNodeMovementTests {

        @Test
        @DisplayName("Should update intersection status when nodes move")
        void shouldUpdateIntersectionWhenNodesMove() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0), true);
            Node nodeB1 = new Node(new Point2D.Double(100, 100), true);
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(0, 100), true);
            Node nodeB2 = new Node(new Point2D.Double(100, 0), true);
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertTrue(edge1.hasIntersection(edge2));

            nodeB2.setPosition(new Point2D.Double(150, 150));

            assertFalse(edge1.hasIntersection(edge2));
        }

        @Test
        @DisplayName("Should detect intersection after node repositioning creates crossing")
        void shouldDetectIntersectionAfterRepositioning() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0), true);
            Node nodeB1 = new Node(new Point2D.Double(50, 0), true);
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(0, 50), true);
            Node nodeB2 = new Node(new Point2D.Double(50, 50), true);
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertFalse(edge1.hasIntersection(edge2));

            nodeB2.setPosition(new Point2D.Double(25, -25));

            assertTrue(edge1.hasIntersection(edge2));
        }

        @Test
        @DisplayName("Should handle multiple node movements")
        void shouldHandleMultipleNodeMovements() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0), true);
            Node nodeB1 = new Node(new Point2D.Double(100, 100), true);
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(0, 100), true);
            Node nodeB2 = new Node(new Point2D.Double(100, 0), true);
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertTrue(edge1.hasIntersection(edge2));

            nodeA2.setPosition(new Point2D.Double(0, 200));
            nodeB2.setPosition(new Point2D.Double(200, 200));
            assertFalse(edge1.hasIntersection(edge2));

            nodeA2.setPosition(new Point2D.Double(0, 0));
            assertTrue(edge1.hasIntersection(edge2));
        }
    }

    @Nested
    @DisplayName("Complex Edge Interaction Tests")
    class ComplexEdgeInteractionTests {

        @Test
        @DisplayName("Should handle edge network with shared nodes")
        void shouldHandleEdgeNetworkWithSharedNodes() {
            Node centerNode = new Node(new Point2D.Double(50, 50), true);
            Node outerNode1 = new Node(new Point2D.Double(10, 10), true);
            Node outerNode2 = new Node(new Point2D.Double(90, 10), true);
            Node outerNode3 = new Node(new Point2D.Double(10, 90), true);
            Node outerNode4 = new Node(new Point2D.Double(90, 90), true);

            Edge radialEdge1 = new Edge(centerNode, outerNode1);
            Edge radialEdge2 = new Edge(centerNode, outerNode2);
            Edge separateEdge1 = new Edge(outerNode3, outerNode4);
            Edge separateEdge2 = new Edge(outerNode1, outerNode2);

            assertFalse(radialEdge1.hasIntersection(radialEdge2));
            assertFalse(radialEdge1.hasIntersection(separateEdge1));
            assertFalse(radialEdge2.hasIntersection(separateEdge1));
            assertFalse(separateEdge1.hasIntersection(separateEdge2));
        }

        @Test
        @DisplayName("Should handle edge movement that resolves multiple intersections")
        void shouldHandleMovementResolvingMultipleIntersections() {
            Node nodeA = new Node(new Point2D.Double(0, 0), true);
            Node nodeB = new Node(new Point2D.Double(100, 100), true);
            Edge mainEdge = new Edge(nodeA, nodeB);

            Node nodeC1 = new Node(new Point2D.Double(0, 100), true);
            Node nodeD1 = new Node(new Point2D.Double(100, 0), true);
            Edge crossingEdge1 = new Edge(nodeC1, nodeD1);

            Node nodeC2 = new Node(new Point2D.Double(25, 75), true);
            Node nodeD2 = new Node(new Point2D.Double(75, 25), true);
            Edge crossingEdge2 = new Edge(nodeC2, nodeD2);

            assertTrue(mainEdge.hasIntersection(crossingEdge1));
            assertTrue(mainEdge.hasIntersection(crossingEdge2));

            nodeC1.setPosition(new Point2D.Double(-100, 100));
            nodeD1.setPosition(new Point2D.Double(0, 200));
            nodeC2.setPosition(new Point2D.Double(150, 150));
            nodeD2.setPosition(new Point2D.Double(200, 200));

            assertFalse(mainEdge.hasIntersection(crossingEdge1));
            assertFalse(mainEdge.hasIntersection(crossingEdge2));
        }
    }

    @Nested
    @DisplayName("Edge Case Integration Tests")
    class EdgeCaseIntegrationTests {

        @Test
        @DisplayName("Should handle edges with nodes at same position but different references")
        void shouldHandleNodesAtSamePosition() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0), true);
            Node nodeB1 = new Node(new Point2D.Double(50, 50), true);
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(50, 50), true);
            Node nodeB2 = new Node(new Point2D.Double(100, 100), true);
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertTrue(edge1.hasIntersection(edge2));
        }

        @Test
        @DisplayName("Should handle very long edges")
        void shouldHandleVeryLongEdges() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0), true);
            Node nodeB1 = new Node(new Point2D.Double(1000, 1000), true);
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(0, 1000), true);
            Node nodeB2 = new Node(new Point2D.Double(1000, 0), true);
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertTrue(edge1.hasIntersection(edge2));
        }

        @Test
        @DisplayName("Should handle very short edges")
        void shouldHandleVeryShortEdges() {
            Node nodeA1 = new Node(new Point2D.Double(0, 0), true);
            Node nodeB1 = new Node(new Point2D.Double(1, 1), true);
            Edge edge1 = new Edge(nodeA1, nodeB1);

            Node nodeA2 = new Node(new Point2D.Double(0, 1), true);
            Node nodeB2 = new Node(new Point2D.Double(1, 0), true);
            Edge edge2 = new Edge(nodeA2, nodeB2);

            assertTrue(edge1.hasIntersection(edge2));
        }
    }
}

package unit;

import model.game.logic.IntersectionChecker;
import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IntersectionChecker Unit Tests")
class IntersectionCheckerTest {

    private IntersectionChecker _checker;

    @BeforeEach
    void setUp() {
        _checker = new IntersectionChecker();
    }

    @Nested
    @DisplayName("Empty / Minimal Edge Lists")
    class EmptyEdgeListTests {

        @Test
        @DisplayName("Should return false for empty list")
        void shouldReturnFalseForEmptyList() {
            assertFalse(_checker.hasIntersections(List.of()));
        }

        @Test
        @DisplayName("Should return false for single edge")
        void shouldReturnFalseForSingleEdge() {
            Node a = new Node(new Point2D.Double(0, 0), true);
            Node b = new Node(new Point2D.Double(100, 100), true);
            Edge edge = new Edge(a, b);

            assertFalse(_checker.hasIntersections(List.of(edge)));
        }
    }

    @Nested
    @DisplayName("Non-Intersecting Edges")
    class NonIntersectingTests {

        @Test
        @DisplayName("Should return false for two parallel edges")
        void shouldReturnFalseForParallelEdges() {
            List<Edge> edges = edges(
                0, 0, 50, 0,
                0, 50, 50, 50
            );

            assertFalse(_checker.hasIntersections(edges));
        }

        @Test
        @DisplayName("Should return false for edges sharing an endpoint")
        void shouldReturnFalseForSharedEndpoint() {
            Node a = new Node(new Point2D.Double(0, 0), true);
            Node b = new Node(new Point2D.Double(50, 50), true);
            Node c = new Node(new Point2D.Double(100, 0), true);
            Edge ab = new Edge(a, b);
            Edge bc = new Edge(b, c);

            assertFalse(_checker.hasIntersections(List.of(ab, bc)));
        }

        @Test
        @DisplayName("Should return false for three edges forming a triangle")
        void shouldReturnFalseForTriangle() {
            Node a = new Node(new Point2D.Double(0, 0), true);
            Node b = new Node(new Point2D.Double(100, 0), true);
            Node c = new Node(new Point2D.Double(50, 86.6), true);

            assertFalse(_checker.hasIntersections(
                List.of(new Edge(a, b), new Edge(b, c), new Edge(a, c))
            ));
        }

        @Test
        @DisplayName("Should return false for multiple separate edges")
        void shouldReturnFalseForMultipleSeparateEdges() {
            List<Edge> edges = edges(
                0, 0, 10, 10,
                20, 20, 30, 30,
                40, 40, 50, 50
            );

            assertFalse(_checker.hasIntersections(edges));
        }
    }

    @Nested
    @DisplayName("Intersecting Edges")
    class IntersectingTests {

        @Test
        @DisplayName("Should detect intersection for two crossing edges (X shape)")
        void shouldDetectCrossingEdges() {
            List<Edge> edges = edges(
                0, 0, 100, 100,
                0, 100, 100, 0
            );

            assertTrue(_checker.hasIntersections(edges));
        }

        @Test
        @DisplayName("Should detect intersection for T-junction")
        void shouldDetectTJunction() {
            List<Edge> edges = edges(
                0, 50, 100, 50,
                50, 0, 50, 100
            );

            assertTrue(_checker.hasIntersections(edges));
        }

        @Test
        @DisplayName("Should detect intersection among multiple edges where only two intersect")
        void shouldDetectIntersectionAmongMany() {
            Node farA = new Node(new Point2D.Double(200, 200), true);
            Node farB = new Node(new Point2D.Double(300, 300), true);
            Edge farEdge = new Edge(farA, farB);

            List<Edge> edges = new ArrayList<>();
            edges.add(farEdge);
            edges.addAll(edges(0, 0, 100, 100, 0, 100, 100, 0));

            assertTrue(_checker.hasIntersections(edges));
        }

        @Test
        @DisplayName("Should detect intersection for collinear overlapping edges")
        void shouldDetectCollinearOverlap() {
            List<Edge> edges = edges(
                0, 0, 100, 0,
                50, 0, 150, 0
            );

            assertTrue(_checker.hasIntersections(edges));
        }
    }

    @Nested
    @DisplayName("Shared Node Exemption")
    class SharedNodeExemptionTests {

        @Test
        @DisplayName("Should NOT count edges sharing nodeA as intersecting")
        void shouldNotCountSharedNodeA() {
            Node shared = new Node(new Point2D.Double(50, 50), true);
            Node other1 = new Node(new Point2D.Double(0, 0), true);
            Node other2 = new Node(new Point2D.Double(100, 100), true);

            Edge e1 = new Edge(shared, other1);
            Edge e2 = new Edge(shared, other2);

            assertFalse(_checker.hasIntersections(List.of(e1, e2)));
        }

        @Test
        @DisplayName("Should NOT count edges sharing nodeB as intersecting")
        void shouldNotCountSharedNodeB() {
            Node shared = new Node(new Point2D.Double(50, 50), true);
            Node other1 = new Node(new Point2D.Double(0, 0), true);
            Node other2 = new Node(new Point2D.Double(100, 100), true);

            Edge e1 = new Edge(other1, shared);
            Edge e2 = new Edge(other2, shared);

            assertFalse(_checker.hasIntersections(List.of(e1, e2)));
        }

        @Test
        @DisplayName("Should NOT count same edge (identical references) as intersecting")
        void shouldNotCountSameEdge() {
            Node a = new Node(new Point2D.Double(0, 0), true);
            Node b = new Node(new Point2D.Double(100, 100), true);
            Edge edge = new Edge(a, b);

            assertFalse(_checker.hasIntersections(List.of(edge, edge)));
        }
    }

    @Nested
    @DisplayName("Edge Case Geometry")
    class EdgeCaseGeometryTests {

        @Test
        @DisplayName("Should handle very small crossing edges")
        void shouldHandleSmallCrossingEdges() {
            List<Edge> edges = edges(
                0, 0, 2, 2,
                0, 2, 2, 0
            );

            assertTrue(_checker.hasIntersections(edges));
        }

        @Test
        @DisplayName("Should handle very large coordinates")
        void shouldHandleLargeCoordinates() {
            List<Edge> edges = edges(
                0, 0, 1_000_000, 1_000_000,
                0, 1_000_000, 1_000_000, 0
            );

            assertTrue(_checker.hasIntersections(edges));
        }

        @Test
        @DisplayName("Should handle negative coordinates with crossing")
        void shouldHandleNegativeCrossing() {
            List<Edge> edges = edges(
                -50, -50, 50, 50,
                -50, 50, 50, -50
            );

            assertTrue(_checker.hasIntersections(edges));
        }

        @Test
        @DisplayName("Should handle vertical and horizontal cross")
        void shouldHandleVerticalHorizontalCross() {
            List<Edge> edges = edges(
                0, -50, 0, 50,
                -50, 0, 50, 0
            );

            assertTrue(_checker.hasIntersections(edges));
        }

        @Test
        @DisplayName("Should handle edges that barely miss each other")
        void shouldHandleBarelyMiss() {
            List<Edge> edges = edges(
                0, 0, 100, 100,
                0, 100, 100, 200
            );

            assertFalse(_checker.hasIntersections(edges));
        }
    }

    @Nested
    @DisplayName("Star / Web Pattern")
    class StarPatternTests {

        @Test
        @DisplayName("Should detect intersections in a complete graph K4")
        void shouldDetectK4Intersections() {
            Node[] nodes = new Node[4];
            nodes[0] = new Node(new Point2D.Double(0, 0), true);
            nodes[1] = new Node(new Point2D.Double(100, 0), true);
            nodes[2] = new Node(new Point2D.Double(0, 100), true);
            nodes[3] = new Node(new Point2D.Double(100, 100), true);

            List<Edge> edges = List.of(
                new Edge(nodes[0], nodes[3]),
                new Edge(nodes[1], nodes[2]),
                new Edge(nodes[0], nodes[1]),
                new Edge(nodes[2], nodes[3])
            );

            assertTrue(_checker.hasIntersections(edges));
        }

        @Test
        @DisplayName("Should detect no intersections in a simple cycle (square)")
        void shouldDetectNoIntersectionsInCycle() {
            Node[] nodes = new Node[4];
            nodes[0] = new Node(new Point2D.Double(0, 0), true);
            nodes[1] = new Node(new Point2D.Double(100, 0), true);
            nodes[2] = new Node(new Point2D.Double(100, 100), true);
            nodes[3] = new Node(new Point2D.Double(0, 100), true);

            List<Edge> edges = List.of(
                new Edge(nodes[0], nodes[1]),
                new Edge(nodes[1], nodes[2]),
                new Edge(nodes[2], nodes[3]),
                new Edge(nodes[3], nodes[0])
            );

            assertFalse(_checker.hasIntersections(edges));
        }
    }

    private static List<Edge> edges(double... coords) {
        if (coords.length % 4 != 0) {
            throw new IllegalArgumentException(
                "Coordinates must be in groups of 4 (x1,y1,x2,y2), got " + coords.length);
        }
        List<Edge> result = new ArrayList<>();
        for (int i = 0; i < coords.length; i += 4) {
            Node a = new Node(new Point2D.Double(coords[i], coords[i + 1]), true);
            Node b = new Node(new Point2D.Double(coords[i + 2], coords[i + 3]), true);
            result.add(new Edge(a, b));
        }
        return result;
    }
}

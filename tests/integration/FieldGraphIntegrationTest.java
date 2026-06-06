package integration;

import model.game.Field;
import model.units.BreakableEdge;
import model.units.Edge;
import model.units.Node;
import model.units.OverheatingEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration - Field Graph")
class FieldGraphIntegrationTest {

    @Nested
    @DisplayName("Field + Edge Geometry")
    class FieldEdgeGeometryTests {

        @Test
        @DisplayName("Should update Edge geometry when Field moves a Node")
        void shouldUpdateEdgeGeometryWhenNodeMoves() {
            Field field = new Field();
            Node nodeA = new Node(new Point2D.Double(0, 0));
            Node nodeB = new Node(new Point2D.Double(100, 100));
            Edge edge = field.createEdge(nodeA, nodeB);

            double originalX = edge.toLine().getX1();

            nodeA.startDragging();
            nodeA.updateDragging(new Point2D.Double(200, 0));
            nodeA.stopDragging();

            assertNotEquals(originalX, edge.toLine().getX1(), 0.01,
                    "Edge line should reflect new node position");
        }
    }

    @Nested
    @DisplayName("Field Intersection Checks")
    class FieldIntersectionChecks {

        @Test
        @DisplayName("Should detect intersections between crossing field edges")
        void shouldDetectIntersectionsBetweenCrossingEdges() {
            Field field = crossingField();

            assertTrue(field.hasIntersections(),
                    "Crossing field should start with intersections");
        }

        @Test
        @DisplayName("Should detect no intersections after moving crossing edge away")
        void shouldDetectNoIntersectionsAfterMovingApart() {
            Field field = crossingField();
            Node node = field.getNodes().get(2);

            node.startDragging();
            node.updateDragging(new Point2D.Double(0, -100));
            node.stopDragging();

            assertFalse(field.hasIntersections());
        }

        @Test
        @DisplayName("Should update intersection status after single node move")
        void shouldUpdateAfterSingleNodeMove() {
            Field field = crossingField();

            assertTrue(field.hasIntersections(), "Crossing field should start with intersections");

            Node node = field.getNodes().get(2);
            node.startDragging();
            node.updateDragging(new Point2D.Double(0, -100));
            node.stopDragging();

            assertFalse(field.hasIntersections(), "Moving a crossing edge away should remove intersections");
        }

        @Test
        @DisplayName("Should detect inactive breakable edges")
        void shouldDetectInactiveBreakableEdges() {
            Field field = new Field();
            Node nodeA = new Node(new Point2D.Double(0, 0));
            Node nodeB = new Node(new Point2D.Double(100, 100));
            Node nodeC = new Node(new Point2D.Double(0, 100));
            Node nodeD = new Node(new Point2D.Double(100, 0));

            BreakableEdge breakableEdge = field.createBreakableEdge(nodeA, nodeB, 10);
            field.createEdge(nodeC, nodeD);
            nodeB.startDragging();
            nodeB.updateDragging(new Point2D.Double(200, 200));
            nodeB.stopDragging();

            assertFalse(breakableEdge.isActive());
            assertTrue(field.hasInactiveEdges());
            assertFalse(field.hasIntersections());
        }

        @Test
        @DisplayName("Should update overheating edges from node movement")
        void shouldUpdateOverheatingEdgesFromNodeMovement() {
            Field field = new Field();
            Node nodeA = new Node(new Point2D.Double(0, 0));
            Node nodeB = new Node(new Point2D.Double(100, 100));
            Node nodeC = new Node(new Point2D.Double(0, 100));
            Node nodeD = new Node(new Point2D.Double(100, 0));

            OverheatingEdge overheatingEdge = field.createOverheatingEdge(nodeA, nodeB, 60, 10, 100);
            field.createEdge(nodeC, nodeD);

            nodeA.startDragging();
            nodeA.updateDragging(new Point2D.Double(60, 0));

            assertTrue(overheatingEdge.isActive());
            assertEquals(0.25, overheatingEdge.getHeatRatio(), 0.1);

            nodeC.updateDragging(new Point2D.Double(-200, 300));

            assertTrue(overheatingEdge.isActive());
            assertFalse(field.hasInactiveEdges());
        }
    }

    @Nested
    @DisplayName("Field List Tests")
    class FieldListTests {

        @Test
        @DisplayName("Should return unmodifiable node list")
        void shouldReturnUnmodifiableNodeList() {
            Field field = fieldWithOneEdge();

            assertThrows(UnsupportedOperationException.class, () ->
                    field.getNodes().add(new Node(new Point2D.Double(0, 0))));
        }

        @Test
        @DisplayName("Should return unmodifiable edge list")
        void shouldReturnUnmodifiableEdgeList() {
            Field field = fieldWithOneEdge();

            assertThrows(UnsupportedOperationException.class, () ->
                    field.getEdges().add(null));
        }
    }

    private Field crossingField() {
        Field field = new Field();
        field.createEdge(
            new Node(new Point2D.Double(0, 0)),
            new Node(new Point2D.Double(100, 100))
        );
        field.createEdge(
            new Node(new Point2D.Double(0, 100)),
            new Node(new Point2D.Double(100, 0))
        );
        return field;
    }

    private Field fieldWithOneEdge() {
        Field field = new Field();
        field.createEdge(
            new Node(new Point2D.Double(0, 0)),
            new Node(new Point2D.Double(100, 100))
        );
        return field;
    }
}

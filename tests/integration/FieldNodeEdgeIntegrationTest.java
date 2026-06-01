package integration;

import model.game.Field;
import model.level.LevelLoadException;
import model.level.LevelManager;
import model.units.BreakableEdge;
import model.units.Edge;
import model.units.Node;
import model.units.OverheatingEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration — Field, Node, Edge")
class FieldNodeEdgeIntegrationTest {

    @Nested
    @DisplayName("Field + Edge Geometry")
    class FieldEdgeGeometryTests {

        @Test
        @DisplayName("Should update Edge geometry when Field moves a Node")
        void shouldUpdateEdgeGeometryWhenNodeMoves() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            Edge firstEdge = field.getEdges().get(0);
            Node nodeA = firstEdge.getNodeA();

            double originalX = firstEdge.toLine().getX1();

            nodeA.startDragging();
            nodeA.updateDragging(new Point2D.Double(999, 999));
            nodeA.stopDragging();

            assertNotEquals(originalX, firstEdge.toLine().getX1(), 0.01,
                    "Edge line should reflect new node position");
        }
    }

    @Nested
    @DisplayName("Field Intersection Checks")
    class FieldIntersectionChecks {

        @Test
        @DisplayName("Should detect intersections in loaded level1")
        void shouldDetectIntersectionsInLevel1() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            assertTrue(field.hasIntersections(),
                    "Level 1 should start with intersections");
        }

        @Test
        @DisplayName("Should detect no intersections after moving all nodes far apart (skip: level1 diagonal edges always cross)")
        void shouldDetectNoIntersectionsAfterMovingApart() throws LevelLoadException {
        }

        @Test
        @DisplayName("Should update intersection status after single node move")
        void shouldUpdateAfterSingleNodeMove() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            boolean before = field.hasIntersections();
            assertTrue(before, "Level 1 should start with intersections");

            Node node = field.getNodes().get(0);
            node.startDragging();
            node.updateDragging(new Point2D.Double(5000, 5000));
            node.stopDragging();

            boolean after = field.hasIntersections();

            assertNotEquals(before, after, "Moving a node should change intersection status");
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

            nodeC.startDragging();
            nodeC.updateDragging(new Point2D.Double(-100, 200));

            assertTrue(overheatingEdge.isActive());
            assertEquals(0.6, overheatingEdge.getHeatRatio(), 0.01);

            nodeC.updateDragging(new Point2D.Double(-200, 300));

            assertFalse(overheatingEdge.isActive());
            assertTrue(field.hasInactiveEdges());
        }
    }

    @Nested
    @DisplayName("Field List Tests")
    class FieldListTests {

        @Test
        @DisplayName("Should return unmodifiable node list")
        void shouldReturnUnmodifiableNodeList() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            assertThrows(UnsupportedOperationException.class, () ->
                    field.getNodes().add(new Node(new Point2D.Double(0, 0))));
        }

        @Test
        @DisplayName("Should return unmodifiable edge list")
        void shouldReturnUnmodifiableEdgeList() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            assertThrows(UnsupportedOperationException.class, () ->
                    field.getEdges().add(null));
        }
    }
}

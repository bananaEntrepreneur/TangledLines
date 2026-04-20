package integration;

import model.factory.DefaultUnitFactory;
import model.game.Field;
import model.level.Level;
import model.level.LevelLoadException;
import model.level.LevelManager;
import model.level.factory.LevelFactory;
import model.level.loader.JsonLevelLoader;
import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;

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

            moveNode(field, nodeA, new Point2D.Double(999, 999));

            assertNotEquals(originalX, firstEdge.toLine().getX1(), 0.01,
                    "Edge line should reflect new node position");
        }

        @Test
        @DisplayName("Should not move a non-movable node through Field")
        void shouldNotUpdateForNonMovableNode() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            Node nonMovable = field.getNodes().stream()
                    .filter(n -> !n.isMovable())
                    .findFirst()
                    .orElseThrow();
            Point2D originalPos = new Point2D.Double(
                    nonMovable.getPosition().getX(),
                    nonMovable.getPosition().getY()
            );

            moveNode(field, nonMovable, new Point2D.Double(999, 999));

            assertEquals(originalPos.getX(), nonMovable.getPosition().getX(), 0.01);
            assertEquals(originalPos.getY(), nonMovable.getPosition().getY(), 0.01);
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
        @DisplayName("Should detect no intersections after moving all movable nodes far apart")
        void shouldDetectNoIntersectionsAfterMovingApart() throws Exception {
            Level level = new JsonLevelLoader().load("levels/level2.json");
            LevelFactory factory = new LevelFactory(new DefaultUnitFactory());
            Field field = factory.createField(level);

            assertTrue(field.hasIntersections(),
                    "Level 2 should start with intersections");

            double[][] positions = {
                {0, 0}, {0, 1000}, {1000, 1000}, {1000, 0}
            };

            List<Node> nodes = field.getNodes();
            for (int i = 0; i < nodes.size(); i++) {
                moveNode(field, nodes.get(i), new Point2D.Double(positions[i][0], positions[i][1]));
            }

            assertFalse(field.hasIntersections());
        }

        @Test
        @DisplayName("Should update intersection status after single node move")
        void shouldUpdateAfterSingleNodeMove() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            boolean before = field.hasIntersections();
            assertTrue(before, "Level 1 should start with intersections");

            Node movable = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .findFirst()
                    .orElseThrow();
            moveNode(field, movable, new Point2D.Double(5000, 5000));

            boolean after = field.hasIntersections();

            assertNotEquals(before, after, "Moving a node should change intersection status");
        }
    }

    @Nested
    @DisplayName("Field + LevelFactory")
    class FieldLevelFactoryTests {

        @Test
        @DisplayName("Should create Field with independent node references")
        void shouldCreateIndependentNodes() throws Exception {
            Level level = new JsonLevelLoader().load("levels/level1.json");
            LevelFactory factory = new LevelFactory(new DefaultUnitFactory());

            Field field1 = factory.createField(level);
            Field field2 = factory.createField(level);

            for (int i = 0; i < field1.getNodes().size(); i++) {
                assertNotSame(field1.getNodes().get(i), field2.getNodes().get(i),
                        "Fields should not share node instances");
            }
        }

        @Test
        @DisplayName("Should create Field where edges share nodes with the field's node list")
        void shouldShareNodesBetweenEdgesAndField() throws Exception {
            Level level = new JsonLevelLoader().load("levels/level2.json");
            LevelFactory factory = new LevelFactory(new DefaultUnitFactory());
            Field field = factory.createField(level);

            List<Node> fieldNodes = field.getNodes();
            for (Edge edge : field.getEdges()) {
                assertTrue(fieldNodes.contains(edge.getNodeA()));
                assertTrue(fieldNodes.contains(edge.getNodeB()));
            }
        }
    }

    @Nested
    @DisplayName("Field Mutability Constraints")
    class FieldMutabilityTests {

        @Test
        @DisplayName("Should reject moving a node not in the field")
        void shouldRejectNodeNotInField() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            Node externalNode = new Node(new Point2D.Double(0, 0), true);

            boolean result = moveNode(field, externalNode, new Point2D.Double(100, 100));

            assertFalse(result);
        }

        @Test
        @DisplayName("Should reject null node move")
        void shouldRejectNullNode() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            boolean result = moveNode(field, null, new Point2D.Double(100, 100));

            assertFalse(result);
        }

        @Test
        @DisplayName("Should reject null position move")
        void shouldRejectNullPosition() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            Node node = field.getNodes().get(0);

            boolean result = moveNode(field, node, null);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return unmodifiable node list")
        void shouldReturnUnmodifiableNodeList() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Field field = lm.getCurrentField();

            assertThrows(UnsupportedOperationException.class, () ->
                    field.getNodes().add(new Node(new Point2D.Double(0, 0), true)));
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

    private boolean moveNode(Field field, Node node, Point2D position) {
        try {
            var method = Field.class.getDeclaredMethod("moveNode", Node.class, Point2D.class);
            method.setAccessible(true);
            return (boolean) method.invoke(field, node, position);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Unable to invoke Field.moveNode", e);
        }
    }
}

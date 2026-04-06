package integration;

import model.factory.DefaultUnitFactory;
import model.game.Field;
import model.level.Level;
import model.level.factory.LevelFactory;
import model.level.loader.JsonLevelLoader;
import model.level.loader.LevelLoader;
import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration — Level Loading Pipeline")
class LevelLoadingIntegrationTest {

    private LevelLoader _loader;
    private LevelFactory _factory;

    @BeforeEach
    void setUp() {
        _loader = new JsonLevelLoader();
        _factory = new LevelFactory(new DefaultUnitFactory());
    }

    @Nested
    @DisplayName("JSON → Level → Field Pipeline")
    class JsonToFieldPipelineTests {

        @Test
        @DisplayName("Should load level1.json and produce a valid Field")
        void shouldLoadLevel1AndProduceValidField() throws Exception {
            Level level = _loader.load("levels/level1.json");

            assertEquals(3, level.getMaxMoves());
            assertEquals(4, level.getNodes().size());
            assertEquals(6, level.getEdges().size());

            Field field = _factory.createField(level);

            assertEquals(4, field.getNodes().size());
            assertEquals(6, field.getEdges().size());
        }

        @Test
        @DisplayName("Should preserve node positions from JSON through to Field")
        void shouldPreserveNodePositions() throws Exception {
            Level level = _loader.load("levels/level1.json");
            Field field = _factory.createField(level);
            List<Node> nodes = field.getNodes();

            assertEquals(200, nodes.get(0).getPosition().getX(), 0.01);
            assertEquals(100, nodes.get(0).getPosition().getY(), 0.01);
            assertEquals(600, nodes.get(3).getPosition().getX(), 0.01);
            assertEquals(500, nodes.get(3).getPosition().getY(), 0.01);
        }

        @Test
        @DisplayName("Should preserve node movability from JSON through to Field")
        void shouldPreserveNodeMovability() throws Exception {
            Level level = _loader.load("levels/level1.json");
            Field field = _factory.createField(level);
            List<Node> nodes = field.getNodes();

            assertTrue(nodes.get(0).isMovable());
            assertTrue(nodes.get(1).isMovable());
            assertFalse(nodes.get(2).isMovable());
            assertTrue(nodes.get(3).isMovable());
        }

        @Test
        @DisplayName("Should connect edges to the same Node instances in the Field")
        void shouldShareNodeInstances() throws Exception {
            Level level = _loader.load("levels/level2.json");
            Field field = _factory.createField(level);

            List<Node> nodes = field.getNodes();
            for (Edge edge : field.getEdges()) {
                assertTrue(nodes.contains(edge.getNodeA()), "Edge nodeA should be in the field");
                assertTrue(nodes.contains(edge.getNodeB()), "Edge nodeB should be in the field");
            }
        }
    }

    @Nested
    @DisplayName("Multiple Levels Pipeline")
    class MultipleLevelsPipelineTests {

        @Test
        @DisplayName("Should load all three levels with correct configurations")
        void shouldLoadAllLevels() throws Exception {
            Level level1 = _loader.load("levels/level1.json");
            Level level2 = _loader.load("levels/level2.json");
            Level level3 = _loader.load("levels/level3.json");

            assertNotEquals(level1.getMaxMoves(), level2.getMaxMoves());
            assertNotEquals(level1.getMaxMoves(), level3.getMaxMoves());
        }

        @Test
        @DisplayName("Should produce independent Fields for each level")
        void shouldProduceIndependentFields() throws Exception {
            Field field1 = _factory.createField(_loader.load("levels/level1.json"));
            Field field2 = _factory.createField(_loader.load("levels/level2.json"));

            assertNotSame(field1, field2);

            for (int i = 0; i < field1.getNodes().size(); i++) {
                assertNotSame(field1.getNodes().get(i), field2.getNodes().get(i));
            }
        }

        @Test
        @DisplayName("Should reflect level edge counts in Fields")
        void shouldReflectEdgeCounts() throws Exception {
            Level level1 = _loader.load("levels/level1.json");
            Level level2 = _loader.load("levels/level2.json");

            Field field1 = _factory.createField(level1);
            Field field2 = _factory.createField(level2);

            assertEquals(6, field1.getEdges().size());
            assertEquals(4, field2.getEdges().size());
        }
    }

    @Nested
    @DisplayName("Edge Geometry After Loading")
    class EdgeGeometryTests {

        @Test
        @DisplayName("Should produce edges with correct line coordinates")
        void shouldProduceCorrectEdgeLines() throws Exception {
            Level level = _loader.load("levels/level1.json");
            Field field = _factory.createField(level);

            Edge firstEdge = field.getEdges().get(0);

            assertEquals(200, firstEdge.toLine().getX1(), 0.01);
            assertEquals(100, firstEdge.toLine().getY1(), 0.01);
        }

        @Test
        @DisplayName("Should have edges referencing correct nodes")
        void shouldHaveCorrectEdgeConnections() throws Exception {
            Level level = _loader.load("levels/level2.json");
            Field field = _factory.createField(level);

            List<Edge> edges = field.getEdges();
            List<Node> nodes = field.getNodes();

            Edge firstEdge = edges.get(0);
            assertSame(nodes.get(0), firstEdge.getNodeA());
            assertSame(nodes.get(3), firstEdge.getNodeB());
        }
    }
}

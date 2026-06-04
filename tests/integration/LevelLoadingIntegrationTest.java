package integration;

import model.game.Field;
import model.level.Level;
import model.level.LevelFactory;
import model.level.loader.JsonLevelLoader;
import model.level.loader.LevelLoader;
import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration — Level Loading Pipeline")
class LevelLoadingIntegrationTest {

    @TempDir
    private Path _levelsDirectory;
    private LevelLoader _loader;
    private LevelFactory _factory;

    @BeforeEach
    void setUp() {
        _loader = new JsonLevelLoader();
        _factory = new LevelFactory();
    }

    @Nested
    @DisplayName("JSON → Level → Field Pipeline")
    class JsonToFieldPipelineTests {

        @Test
        @DisplayName("Should load JSON and produce a valid Field")
        void shouldLoadJsonAndProduceValidField() throws Exception {
            Level level = _loader.load(writeLevel("level1.json", levelWithTwoEdges(7)).toString());

            assertEquals(7, level.getMaxMoves());
            assertEquals(4, level.getNodes().size());
            assertEquals(2, level.getEdgeSpecs().size());

            Field field = _factory.createField(level);

            assertEquals(4, field.getNodes().size());
            assertEquals(2, field.getEdges().size());
        }

        @Test
        @DisplayName("Should preserve node positions from JSON through to Field")
        void shouldPreserveNodePositions() throws Exception {
            Level level = _loader.load(writeLevel("level1.json", levelWithTwoEdges(7)).toString());
            Field field = _factory.createField(level);
            List<Node> nodes = field.getNodes();

            assertEquals(10, nodes.get(0).getPosition().getX(), 0.01);
            assertEquals(20, nodes.get(0).getPosition().getY(), 0.01);
            assertEquals(70, nodes.get(3).getPosition().getX(), 0.01);
            assertEquals(80, nodes.get(3).getPosition().getY(), 0.01);
        }

        @Test
        @DisplayName("Should connect edges to the same Node instances in the Field")
        void shouldShareNodeInstances() throws Exception {
            Level level = _loader.load(writeLevel("level1.json", levelWithTwoEdges(7)).toString());
            Field field = _factory.createField(level);

            List<Node> nodes = field.getNodes();
            for (Edge edge : field.getEdges()) {
                assertTrue(nodes.contains(edge.getNodeA()), "Edge nodeA should be in the field");
                assertTrue(nodes.contains(edge.getNodeB()), "Edge nodeB should be in the field");
            }
        }

        @Test
        @DisplayName("Should load custom numeric edge parameters from JSON")
        void shouldLoadCustomNumericEdgeParameters() throws Exception {
            Path levelFile = writeLevel("custom-edge-params.json", """
                {
                  "maxMoves": 3,
                  "nodes": [
                    { "x": 0, "y": 0 },
                    { "x": 100, "y": 0 }
                  ],
                  "edgeSpecs": [
                    {
                      "nodeA": 0,
                      "nodeB": 1,
                      "type": "elastic",
                      "elasticity": 2.5,
                      "warningPercent": 80
                    }
                  ]
                }
                """);

            Level level = _loader.load(levelFile.toString());
            Level.EdgeSpec edgeSpec = level.getEdgeSpecs().get(0);

            assertEquals("elastic", edgeSpec.type());
            assertEquals(2.5, edgeSpec.parameters().getCustom("elasticity"), 0.01);
            assertEquals(80.0, edgeSpec.parameters().getCustom("warningPercent"), 0.01);
        }

        @Test
        @DisplayName("Should keep edge specs as the single edge representation")
        void shouldKeepEdgeSpecsAsSingleEdgeRepresentation() {
            Level.EdgeSpec edgeSpec = new Level.EdgeSpec(0, 1, "elastic", Map.of("elasticity", 2.0));
            Level level = new Level(
                3,
                List.of(
                    new Level.NodeData(0, 0),
                    new Level.NodeData(100, 0)
                ),
                List.of(edgeSpec)
            );

            assertEquals(List.of(edgeSpec), level.getEdgeSpecs());
        }
    }

    @Nested
    @DisplayName("Multiple Levels Pipeline")
    class MultipleLevelsPipelineTests {

        @Test
        @DisplayName("Should load multiple level files with correct configurations")
        void shouldLoadMultipleLevels() throws Exception {
            Level level1 = _loader.load(writeLevel("level1.json", levelWithTwoEdges(3)).toString());
            Level level2 = _loader.load(writeLevel("level2.json", levelWithOneEdge(4)).toString());
            Level level3 = _loader.load(writeLevel("level3.json", levelWithNoEdges(5)).toString());

            assertEquals(3, level1.getMaxMoves());
            assertEquals(4, level2.getMaxMoves());
            assertEquals(5, level3.getMaxMoves());
        }

        @Test
        @DisplayName("Should produce independent Fields for each level")
        void shouldProduceIndependentFields() throws Exception {
            Field field1 = _factory.createField(_loader.load(writeLevel("level1.json", levelWithTwoEdges(3)).toString()));
            Field field2 = _factory.createField(_loader.load(writeLevel("level2.json", levelWithTwoEdges(4)).toString()));

            assertNotSame(field1, field2);

            for (int i = 0; i < field1.getNodes().size(); i++) {
                assertNotSame(field1.getNodes().get(i), field2.getNodes().get(i));
            }
        }

        @Test
        @DisplayName("Should reflect level edge counts in Fields")
        void shouldReflectEdgeCounts() throws Exception {
            Level level1 = _loader.load(writeLevel("level1.json", levelWithTwoEdges(3)).toString());
            Level level2 = _loader.load(writeLevel("level2.json", levelWithOneEdge(4)).toString());

            Field field1 = _factory.createField(level1);
            Field field2 = _factory.createField(level2);

            assertEquals(2, field1.getEdges().size());
            assertEquals(1, field2.getEdges().size());
        }
    }

    @Nested
    @DisplayName("Edge Geometry After Loading")
    class EdgeGeometryTests {

        @Test
        @DisplayName("Should produce edges with correct line coordinates")
        void shouldProduceCorrectEdgeLines() throws Exception {
            Level level = _loader.load(writeLevel("level1.json", levelWithTwoEdges(3)).toString());
            Field field = _factory.createField(level);

            Edge firstEdge = field.getEdges().get(0);

            assertEquals(10, firstEdge.toLine().getX1(), 0.01);
            assertEquals(20, firstEdge.toLine().getY1(), 0.01);
        }

        @Test
        @DisplayName("Should have edges referencing correct nodes")
        void shouldHaveCorrectEdgeConnections() throws Exception {
            Level level = _loader.load(writeLevel("level1.json", levelWithOneEdge(3)).toString());
            Field field = _factory.createField(level);

            List<Edge> edges = field.getEdges();
            List<Node> nodes = field.getNodes();

            Edge firstEdge = edges.get(0);
            assertSame(nodes.get(0), firstEdge.getNodeA());
            assertSame(nodes.get(1), firstEdge.getNodeB());
        }
    }

    private Path writeLevel(String fileName, String json) throws IOException {
        Path levelFile = _levelsDirectory.resolve(fileName);
        Files.writeString(levelFile, json);
        return levelFile;
    }

    private String levelWithTwoEdges(int maxMoves) {
        return """
            {
              "maxMoves": %d,
              "nodes": [
                { "x": 10, "y": 20 },
                { "x": 30, "y": 40 },
                { "x": 50, "y": 60 },
                { "x": 70, "y": 80 }
              ],
              "edges": [
                { "nodeA": 0, "nodeB": 1 },
                { "nodeA": 2, "nodeB": 3 }
              ]
            }
            """.formatted(maxMoves);
    }

    private String levelWithOneEdge(int maxMoves) {
        return """
            {
              "maxMoves": %d,
              "nodes": [
                { "x": 10, "y": 20 },
                { "x": 30, "y": 40 },
                { "x": 50, "y": 60 },
                { "x": 70, "y": 80 }
              ],
              "edges": [
                { "nodeA": 0, "nodeB": 1 }
              ]
            }
            """.formatted(maxMoves);
    }

    private String levelWithNoEdges(int maxMoves) {
        return """
            {
              "maxMoves": %d,
              "nodes": [
                { "x": 10, "y": 20 },
                { "x": 30, "y": 40 }
              ],
              "edges": []
            }
            """.formatted(maxMoves);
    }
}

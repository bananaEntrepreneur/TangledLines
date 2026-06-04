package integration;

import model.game.Field;
import model.level.LevelLoadException;
import model.level.LevelManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration — LevelManager")
class LevelManagerIntegrationTest {

    @TempDir
    private Path _levelsDirectory;
    private LevelManager _levelManager;

    @BeforeEach
    void setUp() throws IOException, LevelLoadException {
        writeLevel(1, 3, 4, 2);
        writeLevel(2, 4, 3, 1);
        writeLevel(3, 5, 2, 0);
        _levelManager = new LevelManager(_levelsDirectory.toString());
    }

    @Nested
    @DisplayName("Level Progression")
    class LevelProgressionTests {

        @Test
        @DisplayName("Should start at level 0 with hasNextLevel true")
        void shouldStartAtLevelZero() {
            assertEquals(0, _levelManager.getCurrentLevelIndex());
            assertTrue(_levelManager.hasNextLevel());
        }

        @Test
        @DisplayName("Should progress through all levels sequentially")
        void shouldProgressAllLevels() {
            assertEquals(0, _levelManager.getCurrentLevelIndex());

            assertNotNull(_levelManager.nextField());
            assertEquals(1, _levelManager.getCurrentLevelIndex());

            assertNotNull(_levelManager.nextField());
            assertEquals(2, _levelManager.getCurrentLevelIndex());

            assertFalse(_levelManager.hasNextLevel());
            assertNull(_levelManager.nextField());
            assertEquals(2, _levelManager.getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should report level count from available level files")
        void shouldReportTotalLevels() {
            assertEquals(3, _levelManager.getTotalLevels());
        }
    }

    @Nested
    @DisplayName("Max Moves Per Level")
    class MaxMovesPerLevelTests {

        @Test
        @DisplayName("Should return correct maxMoves for each level")
        void shouldReturnCorrectMaxMoves() {
            assertEquals(3, _levelManager.getCurrentMaxMoves());

            _levelManager.nextField();
            assertEquals(4, _levelManager.getCurrentMaxMoves());

            _levelManager.nextField();
            assertEquals(5, _levelManager.getCurrentMaxMoves());
        }
    }

    @Nested
    @DisplayName("Field Creation")
    class FieldCreationTests {

        @Test
        @DisplayName("Should create a fresh Field on each getCurrentField call")
        void shouldCreateFreshField() {
            Field field1 = _levelManager.getCurrentField();
            Field field2 = _levelManager.getCurrentField();

            assertNotSame(field1, field2, "Each call should produce a new Field");
            assertEquals(field1.getNodes().size(), field2.getNodes().size());
            assertEquals(field1.getEdges().size(), field2.getEdges().size());
        }

        @Test
        @DisplayName("Should produce Fields with correct node counts per level")
        void shouldProduceCorrectNodeCounts() {
            Field level1 = _levelManager.getCurrentField();
            assertEquals(4, level1.getNodes().size());

            _levelManager.nextField();
            Field level2 = _levelManager.getCurrentField();
            assertEquals(3, level2.getNodes().size());

            _levelManager.nextField();
            Field level3 = _levelManager.getCurrentField();
            assertEquals(2, level3.getNodes().size());
        }

        @Test
        @DisplayName("Should produce Fields with correct edge counts per level")
        void shouldProduceCorrectEdgeCounts() {
            Field level1 = _levelManager.getCurrentField();
            assertEquals(2, level1.getEdges().size());

            _levelManager.nextField();
            Field level2 = _levelManager.getCurrentField();
            assertEquals(1, level2.getEdges().size());

            _levelManager.nextField();
            Field level3 = _levelManager.getCurrentField();
            assertEquals(0, level3.getEdges().size());
        }
    }

    private void writeLevel(int number, int maxMoves, int nodeCount, int edgeCount) throws IOException {
        Files.writeString(_levelsDirectory.resolve("level" + number + ".json"), levelJson(maxMoves, nodeCount, edgeCount));
    }

    private String levelJson(int maxMoves, int nodeCount, int edgeCount) {
        StringBuilder nodes = new StringBuilder();
        for (int i = 0; i < nodeCount; i++) {
            if (i > 0) {
                nodes.append(",");
            }
            nodes.append("""
                
                    { "x": %d, "y": %d }""".formatted(i * 100, i * 50));
        }

        StringBuilder edges = new StringBuilder();
        for (int i = 0; i < edgeCount; i++) {
            if (i > 0) {
                edges.append(",");
            }
            edges.append("""
                
                    { "nodeA": %d, "nodeB": %d }""".formatted(i, i + 1));
        }

        return """
            {
              "maxMoves": %d,
              "nodes": [%s
              ],
              "edges": [%s
              ]
            }
            """.formatted(maxMoves, nodes, edges);
    }
}

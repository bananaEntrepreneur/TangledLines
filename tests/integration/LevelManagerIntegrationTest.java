package integration;

import model.game.Field;
import model.level.LevelLoadException;
import model.level.LevelManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration — LevelManager")
class LevelManagerIntegrationTest {

    private LevelManager _levelManager;

    @BeforeEach
    void setUp() throws LevelLoadException {
        _levelManager = new LevelManager("levels");
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
        }

        @Test
        @DisplayName("Should report correct total level count")
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
            assertEquals(5, _levelManager.getCurrentMaxMoves());

            _levelManager.nextField();
            assertEquals(1, _levelManager.getCurrentMaxMoves());
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
            assertEquals(4, level2.getNodes().size());

            _levelManager.nextField();
            Field level3 = _levelManager.getCurrentField();
            assertEquals(4, level3.getNodes().size());
        }

        @Test
        @DisplayName("Should produce Fields with correct edge counts per level")
        void shouldProduceCorrectEdgeCounts() {
            Field level1 = _levelManager.getCurrentField();
            assertEquals(6, level1.getEdges().size());

            _levelManager.nextField();
            Field level2 = _levelManager.getCurrentField();
            assertEquals(4, level2.getEdges().size());
        }
    }
}

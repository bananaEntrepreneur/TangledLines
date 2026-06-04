package integration;

import model.game.Field;
import model.game.Game;
import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.LevelLoadException;
import model.level.LevelManager;
import model.units.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration — Game + LevelManager")
class GameIntegrationTest {

    @TempDir
    private Path _levelsDirectory;
    private Game _game;

    @BeforeEach
    void setUp() throws IOException, LevelLoadException {
        Files.writeString(_levelsDirectory.resolve("level1.json"), crossingLevel(3));
        Files.writeString(_levelsDirectory.resolve("level2.json"), clearLevel(4));
        _game = new Game(new LevelManager(_levelsDirectory.toString()));
    }

    private GameState _state() { return _game.getState(); }
    private LevelNavigation _nav() { return _game.getNavigation(); }
    private Field _field() { return _state().getField(); }

    @Nested
    @DisplayName("Game Initialization from LevelManager")
    class GameInitializationTests {

        @Test
        @DisplayName("Should reflect LevelManager's total level count")
        void shouldReflectLevelCount() {
            assertEquals(2, _nav().getTotalLevels());
            assertEquals(0, _nav().getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should initialize with level1's configuration")
        void shouldInitializeWithLevel1() {
            assertEquals(4, _field().getNodes().size());
            assertEquals(2, _field().getEdges().size());
            assertEquals(3, _state().getMaxMoves());
        }

        @Test
        @DisplayName("Should start with intersections in level1")
        void shouldStartWithIntersections() {
            assertTrue(_field().hasIntersections());
        }
    }

    @Nested
    @DisplayName("Level Transition")
    class LevelTransitionTests {

        @Test
        @DisplayName("Should not allow nextLevel without winning")
        void shouldNotAllowNextWithoutWin() {
            assertFalse(_nav().nextLevel());
            assertEquals(0, _nav().getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should transition to level2 after winning level1")
        void shouldTransitionToLevel2() {
            winCrossingLevel();

            assertTrue(_nav().nextLevel());
            assertEquals(1, _nav().getCurrentLevelIndex());
            assertEquals(0, _state().getMoveCount());
            assertEquals(4, _state().getMaxMoves());
        }

        @Test
        @DisplayName("Should restart level with reset move count and original maxMoves")
        void shouldRestartWithReset() {
            moveKeepingIntersection(10);
            assertEquals(1, _state().getMoveCount());
            assertFalse(_state().isGameOver());

            _nav().restartLevel();

            assertEquals(0, _state().getMoveCount());
            assertEquals(3, _state().getMaxMoves());
            assertFalse(_state().isGameOver());
        }

        @Test
        @DisplayName("Should detach from old field nodes after restart")
        void shouldDetachFromOldFieldNodesAfterRestart() {
            Field oldField = _field();

            _nav().restartLevel();

            Node oldNode = oldField.getNodes().get(0);
            drag(oldNode, new Point2D.Double(50, 50));

            assertEquals(0, _state().getMoveCount());
            assertSame(_state().getField(), _field());
            assertNotSame(oldField, _field());
        }
    }

    @Nested
    @DisplayName("Gameplay Rules")
    class GameplayRuleTests {

        @Test
        @DisplayName("Should complete all levels after winning the last level")
        void shouldCompleteAllLevels() {
            winCrossingLevel();
            assertTrue(_nav().nextLevel());

            moveInClearLevel();
            assertTrue(_state().isWin());
            assertFalse(_nav().nextLevel());

            assertTrue(_state().isAllLevelsComplete());
        }

        @Test
        @DisplayName("Should lose when move limit is reached while intersections remain")
        void shouldEnforceStrictMoveLimit() {
            moveKeepingIntersection(10);
            moveKeepingIntersection(20);
            moveKeepingIntersection(30);

            assertTrue(_field().hasIntersections());
            assertEquals(3, _state().getMoveCount());
            assertTrue(_state().isGameOver());
            assertFalse(_state().isWin());
        }

    }

    private void winCrossingLevel() {
        Node node = _field().getNodes().get(2);
        drag(node, new Point2D.Double(0, -100));

        assertFalse(_field().hasIntersections());
        assertTrue(_state().isWin());
    }

    private void moveKeepingIntersection(double x) {
        Node node = _field().getNodes().get(0);
        drag(node, new Point2D.Double(x, 0));
    }

    private void moveInClearLevel() {
        Node node = _field().getNodes().get(0);
        drag(node, new Point2D.Double(50, 0));
    }

    private void drag(Node node, Point2D position) {
        node.startDragging();
        node.updateDragging(position);
        node.stopDragging();
    }

    private String crossingLevel(int maxMoves) {
        return """
            {
              "maxMoves": %d,
              "nodes": [
                { "x": 0, "y": 0 },
                { "x": 100, "y": 100 },
                { "x": 0, "y": 100 },
                { "x": 100, "y": 0 }
              ],
              "edges": [
                { "nodeA": 0, "nodeB": 1 },
                { "nodeA": 2, "nodeB": 3 }
              ]
            }
            """.formatted(maxMoves);
    }

    private String clearLevel(int maxMoves) {
        return """
            {
              "maxMoves": %d,
              "nodes": [
                { "x": 0, "y": 0 },
                { "x": 100, "y": 0 }
              ],
              "edges": [
                { "nodeA": 0, "nodeB": 1 }
              ]
            }
            """.formatted(maxMoves);
    }

}

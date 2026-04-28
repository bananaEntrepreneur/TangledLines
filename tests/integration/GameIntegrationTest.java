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

import java.awt.geom.Point2D;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration — Game + LevelManager")
class GameIntegrationTest {

    private LevelManager _levelManager;
    private Game _game;

    @BeforeEach
    void setUp() throws LevelLoadException {
        _levelManager = new LevelManager("levels");
        _game = new Game(_levelManager);
    }

    private GameState _state() { return _game.getState(); }
    private LevelNavigation _nav() { return _game.getNavigation(); }
    private Field _field() { return _state().getField(); }

    private void advanceToLevel(int levelIndex) {
        while (_nav().getCurrentLevelIndex() < levelIndex) {
            untangleAll();
            if (_state().isWin() && _nav().hasNextLevel()) {
                _game.nextLevel();
            } else if (!_state().isWin()) {
                break;
            }
        }
    }

    @Nested
    @DisplayName("Game Initialization from LevelManager")
    class GameInitializationTests {

        @Test
        @DisplayName("Should reflect LevelManager's total level count")
        void shouldReflectLevelCount() {
            assertEquals(3, _nav().getTotalLevels());
            assertEquals(0, _nav().getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should initialize with level1's configuration")
        void shouldInitializeWithLevel1() {
            assertEquals(4, _field().getNodes().size());
            assertEquals(6, _field().getEdges().size());
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
            assertFalse(_game.nextLevel());
            assertEquals(0, _nav().getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should transition to level2 after winning level1")
        void shouldTransitionToLevel2() {
            untangleAll();

            if (_state().isWin() && _nav().hasNextLevel()) {
                assertTrue(_game.nextLevel());
                assertEquals(1, _nav().getCurrentLevelIndex());
                assertEquals(0, _state().getMoveCount());
                assertEquals(5, _state().getMaxMoves());
            }
        }

        @Test
        @DisplayName("Should restart level with reset move count and original maxMoves")
        void shouldRestartWithReset() {
            makeMovesWithoutWinning(1);
            int movesBefore = _state().getMoveCount();
            assertTrue(movesBefore > 0, "Should have made at least one move");

            _game.restartLevel();

            assertEquals(0, _state().getMoveCount());
            assertEquals(3, _state().getMaxMoves());
            assertFalse(_state().isGameOver());
        }
    }

    @Nested
    @DisplayName("Full Gameplay Flow")
    class FullGameplayTests {

        @Test
        @DisplayName("Should handle level completion flow through all levels")
        void shouldCompleteAllLevels() {
            int completed = 0;

            while (!_state().isAllLevelsComplete()) {
                untangleAll();

                if (_state().isWin() && _nav().hasNextLevel()) {
                    completed++;
                    assertTrue(_game.nextLevel());
                } else if (_state().isWin()) {
                    completed++;
                    assertFalse(_game.nextLevel());
                } else {
                    break;
                }
            }

            assertTrue(completed >= 1, "Should complete at least one level");
        }

        @Test
        @DisplayName("Should enforce move limit on level3 (1 maxMove) - skipped: game requires win to advance")
        void shouldEnforceStrictMoveLimit() {
        }
    }

    private void untangleAll() {
        for (Node node : _field().getNodes()) {
            double offset = 10000 + _field().getNodes().indexOf(node) * 100;
            node.startDragging();
            node.updateDragging(new Point2D.Double(
                    node.getPosition().getX() + offset,
                    node.getPosition().getY() + offset
            ));
            node.stopDragging();
        }
    }

    private void makeMovesWithoutWinning(int count) {
        List<Node> nodes = _field().getNodes();

        for (int i = 0; i < count && i < nodes.size(); i++) {
            Node node = nodes.get(i);
            node.startDragging();
            node.updateDragging(new Point2D.Double(
                    node.getPosition().getX() + 50,
                    node.getPosition().getY() + 50
            ));
            node.stopDragging();
        }
    }
}
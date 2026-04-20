package integration;

import model.game.Field;
import model.game.Game;
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

    @Nested
    @DisplayName("Game Initialization from LevelManager")
    class GameInitializationTests {

        @Test
        @DisplayName("Should reflect LevelManager's total level count")
        void shouldReflectLevelCount() {
            assertEquals(3, _game.getTotalLevels());
            assertEquals(0, _game.getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should initialize with level1's configuration")
        void shouldInitializeWithLevel1() {
            assertEquals(4, _game.getField().getNodes().size());
            assertEquals(6, _game.getField().getEdges().size());
            assertEquals(3, _game.getMaxMoves());
        }

        @Test
        @DisplayName("Should start with intersections in level1")
        void shouldStartWithIntersections() {
            assertTrue(_game.getField().hasIntersections());
        }
    }

    @Nested
    @DisplayName("Level Transition")
    class LevelTransitionTests {

        @Test
        @DisplayName("Should not allow nextLevel without winning")
        void shouldNotAllowNextWithoutWin() {
            assertFalse(_game.nextLevel());
            assertEquals(0, _game.getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should transition to level2 after winning level1")
        void shouldTransitionToLevel2() {
            untangleAll();

            if (_game.isWin() && _game.hasNextLevel()) {
                assertTrue(_game.nextLevel());
                assertEquals(1, _game.getCurrentLevelIndex());
                assertEquals(0, _game.getMoveCount());
                assertEquals(5, _game.getMaxMoves());
            }
        }

        @Test
        @DisplayName("Should restart level with reset move count and original maxMoves")
        void shouldRestartWithReset() {
            makeMovesWithoutWinning(1);
            int movesBefore = _game.getMoveCount();
            assertTrue(movesBefore > 0, "Should have made at least one move");

            _game.restartLevel();

            assertEquals(0, _game.getMoveCount());
            assertEquals(3, _game.getMaxMoves());
            assertFalse(_game.isGameOver());
        }
    }

    @Nested
    @DisplayName("Full Gameplay Flow")
    class FullGameplayTests {

        @Test
        @DisplayName("Should handle level completion flow through all levels")
        void shouldCompleteAllLevels() {
            int completed = 0;

            while (!_game.isAllLevelsComplete()) {
                untangleAll();

                if (_game.isWin() && _game.hasNextLevel()) {
                    completed++;
                    assertTrue(_game.nextLevel());
                } else if (_game.isWin()) {
                    completed++;
                    assertFalse(_game.nextLevel());
                } else {
                    break;
                }
            }

            assertTrue(completed >= 1, "Should complete at least one level");
        }

        @Test
        @DisplayName("Should enforce move limit on level3 (1 maxMove)")
        void shouldEnforceStrictMoveLimit() {
            _game.restartLevel();

            Field field = _game.getField();
            Node movable = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .findFirst()
                    .orElseThrow();

            moveThroughGame(movable, new Point2D.Double(
                    movable.getPosition().getX() + 500,
                    movable.getPosition().getY() + 500
            ));

            assertTrue(_game.isGameOver());
            assertEquals(1, _game.getMoveCount());
        }
    }

    private void untangleAll() {
        attachGameToField();
        Field field = _game.getField();
        List<Node> movable = field.getNodes().stream()
                .filter(Node::isMovable)
                .toList();

        for (Node node : movable) {
            double offset = 10000 + movable.indexOf(node) * 100;
            node.move(new Point2D.Double(
                    node.getPosition().getX() + offset,
                    node.getPosition().getY() + offset
            ));
        }
    }

    private void makeMovesWithoutWinning(int count) {
        attachGameToField();
        Field field = _game.getField();
        List<Node> movable = field.getNodes().stream()
                .filter(Node::isMovable)
                .toList();

        for (int i = 0; i < count && i < movable.size(); i++) {
            Node node = movable.get(i);
            node.move(new Point2D.Double(
                    node.getPosition().getX() + 50,
                    node.getPosition().getY() + 50
            ));
        }
    }

    private boolean moveThroughGame(Node node, Point2D position) {
        attachGameToField();
        return node.move(position);
    }

    private void attachGameToField() {
        for (Node node : _game.getField().getNodes()) {
            node.removeListener(_game);
            node.addListener(_game);
        }
    }
}

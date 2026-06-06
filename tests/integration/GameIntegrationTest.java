package integration;

import model.game.Field;
import model.game.Game;
import model.game.GameState;
import model.game.LevelNavigation;
import model.level.Level;
import model.level.LevelManager;
import model.level.seeder.Seeder;
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

    private Game _game;

    @BeforeEach
    void setUp() {
        _game = new Game(new LevelManager(new TestSeeder()));
    }

    private GameState _state() { return _game.getState(); }
    private LevelNavigation _nav() { return _game.getLevelNavigation(); }
    private Field _field() { return _state().getField(); }

    @Nested
    @DisplayName("Game Initialization from LevelManager")
    class GameInitializationTests {

        @Test
        @DisplayName("Should reflect LevelManager's total level count")
        void shouldReflectLevelCount() {
            assertEquals(2, _nav().getTotalLevelCount());
            assertEquals(0, _nav().getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should initialize with level1's configuration")
        void shouldInitializeWithLevel1() {
            assertEquals(4, _field().getNodes().size());
            assertEquals(2, _field().getEdges().size());
            assertEquals(3, _state().getMaxMoveCount());
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
        @DisplayName("Should not allow advanceToNextLevel without winning")
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
            assertEquals(4, _state().getMaxMoveCount());
        }

        @Test
        @DisplayName("Should restart level with reset move count and original maxMoves")
        void shouldRestartWithReset() {
            moveKeepingIntersection(10);
            assertEquals(1, _state().getMoveCount());
            assertFalse(_state().isCurrentLevelFinished());

            _nav().restartLevel();

            assertEquals(0, _state().getMoveCount());
            assertEquals(3, _state().getMaxMoveCount());
            assertFalse(_state().isCurrentLevelFinished());
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

        @Test
        @DisplayName("Should detach from old field nodes after next level")
        void shouldDetachFromOldFieldNodesAfterNextLevel() {
            Field oldField = _field();
            winCrossingLevel();

            assertTrue(_nav().nextLevel());
            Field currentField = _field();

            Node oldNode = oldField.getNodes().get(0);
            drag(oldNode, new Point2D.Double(50, 50));

            assertEquals(0, _state().getMoveCount());
            assertSame(currentField, _field());
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
            assertTrue(_state().isCurrentLevelWon());
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
            assertTrue(_state().isCurrentLevelFinished());
            assertFalse(_state().isCurrentLevelWon());
        }

    }

    private void winCrossingLevel() {
        Node node = _field().getNodes().get(2);
        drag(node, new Point2D.Double(0, -100));

        assertFalse(_field().hasIntersections());
        assertTrue(_state().isCurrentLevelWon());
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

    private static class TestSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(crossingLevel(3), clearLevel(4));
        }

        private Level crossingLevel(int maxMoves) {
            return createLevel(maxMoves, () -> {
                Field field = createField();
                Node a = createNode(field, 0, 0);
                Node b = createNode(field, 100, 100);
                Node c = createNode(field, 0, 100);
                Node d = createNode(field, 100, 0);
                createEdge(field, a, b);
                createEdge(field, c, d);
                return field;
            });
        }

        private Level clearLevel(int maxMoves) {
            return createLevel(maxMoves, () -> {
                Field field = createField();
                Node a = createNode(field, 0, 0);
                Node b = createNode(field, 100, 0);
                createEdge(field, a, b);
                return field;
            });
        }
    }

}

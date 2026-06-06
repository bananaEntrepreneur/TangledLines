package e2e;

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

@DisplayName("End-to-End Game Tests")
class GameE2ETest {

    private Game _game;

    @BeforeEach
    void setUp() {
        _game = new Game(new LevelManager(new TestSeeder()));
    }

    private GameState _state() { return _game.getState(); }
    private LevelNavigation _nav() { return _game.getLevelNavigation(); }
    private Field _field() { return _state().getField(); }

    @Nested
    @DisplayName("Game Initialization Tests")
    class GameInitializationTests {

        @Test
        @DisplayName("Should initialize with first level loaded")
        void shouldInitializeWithFirstLevel() {
            assertNotNull(_field());
            assertEquals(0, _nav().getCurrentLevelIndex());
            assertEquals(0, _state().getMoveCount());
            assertEquals(3, _state().getMaxMoveCount());
            assertFalse(_state().isCurrentLevelFinished());
            assertFalse(_state().isCurrentLevelWon());
        }

        @Test
        @DisplayName("Should load nodes and edges from seeded level")
        void shouldLoadNodesAndEdgesFromSeededLevel() {
            assertEquals(4, _field().getNodes().size());
            assertEquals(2, _field().getEdges().size());
            assertTrue(_field().hasIntersections());
        }
    }

    @Nested
    @DisplayName("Node Movement Tests")
    class NodeMovementTests {

        @Test
        @DisplayName("Should count committed moves")
        void shouldCountCommittedMoves() {
            moveKeepingIntersection(10);
            moveKeepingIntersection(20);

            assertEquals(2, _state().getMoveCount());
            assertFalse(_state().isCurrentLevelFinished());
        }

        @Test
        @DisplayName("Should ignore moves after game over")
        void shouldIgnoreMovesAfterGameOver() {
            winCrossingLevel();
            int moveCountBefore = _state().getMoveCount();

            drag(_field().getNodes().get(0), new Point2D.Double(50, 0));

            assertEquals(moveCountBefore, _state().getMoveCount());
            assertTrue(_state().isCurrentLevelFinished());
        }
    }

    @Nested
    @DisplayName("Win And Loss Tests")
    class WinAndLossTests {

        @Test
        @DisplayName("Should win when all intersections are resolved within move limit")
        void shouldWinWhenIntersectionsResolved() {
            winCrossingLevel();

            assertTrue(_state().isCurrentLevelWon());
            assertTrue(_state().isCurrentLevelFinished());
        }

        @Test
        @DisplayName("Should lose when move limit is reached with intersections")
        void shouldLoseWhenMoveLimitReached() {
            moveKeepingIntersection(10);
            moveKeepingIntersection(20);
            moveKeepingIntersection(30);

            assertTrue(_field().hasIntersections());
            assertEquals(3, _state().getMoveCount());
            assertTrue(_state().isCurrentLevelFinished());
            assertFalse(_state().isCurrentLevelWon());
        }
    }

    @Nested
    @DisplayName("Level Progression Tests")
    class LevelProgressionTests {

        @Test
        @DisplayName("Should not allow next level without winning")
        void shouldNotAllowNextLevelWithoutWinning() {
            assertFalse(_nav().nextLevel());
            assertEquals(0, _nav().getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should load next level after winning")
        void shouldLoadNextLevelAfterWinning() {
            winCrossingLevel();

            assertTrue(_nav().nextLevel());

            assertEquals(1, _nav().getCurrentLevelIndex());
            assertEquals(0, _state().getMoveCount());
            assertEquals(4, _state().getMaxMoveCount());
            assertFalse(_field().hasIntersections());
        }

        @Test
        @DisplayName("Should complete all levels after winning final level")
        void shouldCompleteAllLevels() {
            winCrossingLevel();
            assertTrue(_nav().nextLevel());

            drag(_field().getNodes().get(0), new Point2D.Double(50, 0));
            assertTrue(_state().isCurrentLevelWon());

            assertFalse(_nav().nextLevel());
            assertTrue(_state().isAllLevelsComplete());
        }
    }

    @Nested
    @DisplayName("Restart Tests")
    class RestartTests {

        @Test
        @DisplayName("Should restart level with original configuration")
        void shouldRestartLevelWithOriginalConfiguration() {
            Field initialField = _field();
            Node node = initialField.getNodes().get(0);
            Point2D originalPosition = node.getPosition();

            moveKeepingIntersection(10);
            assertNotEquals(originalPosition, node.getPosition());

            _nav().restartLevel();

            Field restartedField = _field();
            Node restartedNode = restartedField.getNodes().get(0);
            assertEquals(originalPosition.getX(), restartedNode.getPosition().getX(), 0.01);
            assertEquals(originalPosition.getY(), restartedNode.getPosition().getY(), 0.01);
            assertEquals(0, _state().getMoveCount());
            assertFalse(_state().isCurrentLevelFinished());
        }
    }

    @Nested
    @DisplayName("Edge Intersection Tests")
    class EdgeIntersectionTests {

        @Test
        @DisplayName("Should update intersection status after node movement")
        void shouldUpdateIntersectionStatusAfterMovement() {
            assertTrue(_field().hasIntersections());

            Node node = _field().getNodes().get(2);
            drag(node, new Point2D.Double(0, -100));

            assertFalse(_field().hasIntersections());
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

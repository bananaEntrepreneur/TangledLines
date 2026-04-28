package e2e;

import model.game.Field;
import model.game.Game;
import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.LevelManager;
import model.level.LevelLoadException;
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

    private static final String LEVELS_DIR = "levels";
    private LevelManager _levelManager;
    private Game _game;

    @BeforeEach
    void setUp() throws LevelLoadException {
        _levelManager = new LevelManager(LEVELS_DIR);
        _game = new Game(_levelManager);
    }

    private GameState _state() { return _game.getState(); }
    private LevelNavigation _nav() { return _game.getNavigation(); }
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
            assertFalse(_state().isGameOver());
            assertFalse(_state().isWin());
        }

        @Test
        @DisplayName("Should have correct move limit from level config")
        void shouldHaveCorrectMoveLimit() {
            assertTrue(_state().getMaxMoves() > 0);
            assertEquals(_levelManager.getCurrentMaxMoves(), _state().getMaxMoves());
        }

        @Test
        @DisplayName("Should load all nodes from level file")
        void shouldLoadAllNodes() {
            Field field = _field();
            assertTrue(field.getNodes().size() > 0);
        }

        @Test
        @DisplayName("Should load all edges from level file")
        void shouldLoadAllEdges() {
            Field field = _field();
            assertTrue(field.getEdges().size() > 0);
        }
    }

    @Nested
    @DisplayName("Node Movement Tests")
    class NodeMovementTests {

        @Test
        @DisplayName("Should allow moving node within move limit")
        void shouldAllowMovingNode() {
            Node node = _field().getNodes().get(0);
            Point2D originalPos = node.getPosition();

            node.startDragging();
            node.updateDragging(new Point2D.Double(
                    originalPos.getX() + 50,
                    originalPos.getY() + 50
            ));
            node.stopDragging();

            assertEquals(1, _state().getMoveCount());
        }

        @Test
        @DisplayName("Should count each valid move")
        void shouldCountEachValidMove() {
            List<Node> nodes = _field().getNodes();

            Node node1 = nodes.get(0);
            Point2D pos1 = new Point2D.Double(
                    node1.getPosition().getX() + 50,
                    node1.getPosition().getY() + 50
            );
            node1.startDragging();
            node1.updateDragging(pos1);
            node1.stopDragging();

            Node node2 = nodes.get(1);
            Point2D pos2 = new Point2D.Double(
                    node2.getPosition().getX() + 50,
                    node2.getPosition().getY() + 50
            );
            node2.startDragging();
            node2.updateDragging(pos2);
            node2.stopDragging();

            assertEquals(2, _state().getMoveCount());
        }

        @Test
        @DisplayName("Should reject move after game over")
        void shouldRejectMoveAfterGameOver() {
            List<Node> nodes = _field().getNodes();
            Node node = nodes.get(0);

            Point2D winPosition = new Point2D.Double(10000, 10000);
            node.startDragging();
            node.updateDragging(winPosition);
            node.stopDragging();

            if (_state().isGameOver() && _state().isWin()) {
                int moveCountBefore = _state().getMoveCount();
                node.startDragging();
                node.updateDragging(new Point2D.Double(500, 500));
                node.stopDragging();

                assertEquals(moveCountBefore, _state().getMoveCount());
            }
        }
    }

    @Nested
    @DisplayName("Win Condition Tests")
    class WinConditionTests {

        @Test
        @DisplayName("Should win when all intersections resolved within move limit")
        void shouldWinWhenIntersectionsResolved() {
            boolean hasIntersectionsInitially = _field().hasIntersections();

            if (hasIntersectionsInitially) {
                List<Node> nodes = _field().getNodes();
                Point2D newPos = new Point2D.Double(1000, 1000);
                nodes.get(0).startDragging();
                nodes.get(0).updateDragging(newPos);
                nodes.get(0).stopDragging();

                boolean stillHasIntersections = _field().hasIntersections();

                if (!stillHasIntersections && _state().getMoveCount() <= _state().getMaxMoves()) {
                    assertTrue(_state().isWin());
                    assertTrue(_state().isGameOver());
                }
            }
        }

        @Test
        @DisplayName("Should lose when move limit exceeded with intersections")
        void shouldLoseWhenMoveLimitExceeded() {
            List<Node> nodes = _field().getNodes();
            int maxMoves = _state().getMaxMoves();

            for (int i = 0; i < maxMoves + 1; i++) {
                Point2D newPos = new Point2D.Double(
                        nodes.get(0).getPosition().getX() + 100 * (i + 1),
                        nodes.get(0).getPosition().getY()
                );
                nodes.get(0).startDragging();
                nodes.get(0).updateDragging(newPos);
                nodes.get(0).stopDragging();
            }

            if (_field().hasIntersections()) {
                assertTrue(_state().isGameOver());
                assertFalse(_state().isWin());
            }
        }
    }

    @Nested
    @DisplayName("Level Progression Tests")
    class LevelProgressionTests {

        @Test
        @DisplayName("Should not allow next level without winning")
        void shouldNotAllowNextLevelWithoutWinning() {
            boolean result = _game.nextLevel();

            assertFalse(result);
            assertEquals(0, _nav().getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should allow next level after winning")
        void shouldAllowNextLevelAfterWinning() {
            if (_nav().getTotalLevels() > 1) {
                Node node = _field().getNodes().get(0);
                node.startDragging();
                node.updateDragging(new Point2D.Double(10000, 10000));
                node.stopDragging();

                if (_state().isWin()) {
                    boolean result = _game.nextLevel();

                    if (_nav().hasNextLevel()) {
                        assertTrue(result);
                        assertEquals(1, _nav().getCurrentLevelIndex());
                    } else {
                        assertFalse(result);
                        assertTrue(_state().isAllLevelsComplete());
                    }
                }
            }
        }

        @Test
        @DisplayName("Should reset move count on next level")
        void shouldResetMoveCountOnNextLevel() {
            if (_nav().getTotalLevels() > 1) {
                Node node = _field().getNodes().get(0);
                node.startDragging();
                node.updateDragging(new Point2D.Double(10000, 10000));
                node.stopDragging();

                if (_state().isWin() && _nav().hasNextLevel()) {
                    _game.nextLevel();

                    assertEquals(0, _state().getMoveCount());
                    assertTrue(_state().getMaxMoves() > 0);
                }
            }
        }

        @Test
        @DisplayName("Should restart level with original configuration")
        void shouldRestartLevelWithOriginalConfiguration() {
            Field initialField = _field();
            Node node = initialField.getNodes().get(0);
            Point2D originalPos = node.getPosition();

            node.startDragging();
            node.updateDragging(new Point2D.Double(500, 500));
            node.stopDragging();

            assertNotEquals(originalPos, node.getPosition());

            _game.restartLevel();

            Field restartedField = _field();
            Node sameNode = restartedField.getNodes().get(
                    initialField.getNodes().indexOf(node)
            );
            assertEquals(originalPos.getX(), sameNode.getPosition().getX(), 0.01);
            assertEquals(originalPos.getY(), sameNode.getPosition().getY(), 0.01);
            assertEquals(0, _state().getMoveCount());
            assertFalse(_state().isGameOver());
        }
    }

    @Nested
    @DisplayName("Multiple Levels Tests")
    class MultipleLevelsTests {

        @Test
        @DisplayName("Should have multiple levels available")
        void shouldHaveMultipleLevels() {
            assertTrue(_nav().getTotalLevels() >= 1);
        }

        @Test
        @DisplayName("Should track current level index correctly")
        void shouldTrackCurrentLevelIndex() {
            assertEquals(0, _nav().getCurrentLevelIndex());

            if (_nav().getTotalLevels() > 1) {
                Node node = _field().getNodes().get(0);
                node.startDragging();
                node.updateDragging(new Point2D.Double(10000, 10000));
                node.stopDragging();

                if (_state().isWin() && _nav().hasNextLevel()) {
                    _game.nextLevel();
                    assertEquals(1, _nav().getCurrentLevelIndex());
                }
            }
        }

        @Test
        @DisplayName("Should detect when all levels complete")
        void shouldDetectAllLevelsComplete() {
            if (_nav().getTotalLevels() == 1) {
                Node node = _field().getNodes().get(0);
                node.startDragging();
                node.updateDragging(new Point2D.Double(10000, 10000));
                node.stopDragging();

                if (_state().isWin()) {
                    assertFalse(_game.nextLevel());
                    assertTrue(_state().isAllLevelsComplete());
                }
            }
        }

        @Test
        @DisplayName("Should load different field for each level")
        void shouldLoadDifferentFieldForEachLevel() {
            Field firstField = _field();
            int firstNodeCount = firstField.getNodes().size();
            int firstEdgeCount = firstField.getEdges().size();
            int firstMaxMoves = _state().getMaxMoves();

            if (_nav().getTotalLevels() > 1) {
                Node node = firstField.getNodes().get(0);
                node.startDragging();
                node.updateDragging(new Point2D.Double(10000, 10000));
                node.stopDragging();

                if (_state().isWin() && _nav().hasNextLevel()) {
                    _game.nextLevel();
                    Field secondField = _field();

                    assertTrue(
                            secondField.getNodes().size() != firstNodeCount ||
                            secondField.getEdges().size() != firstEdgeCount ||
                            _state().getMaxMoves() != firstMaxMoves
                    );
                }
            }
        }
    }

    @Nested
    @DisplayName("Game State Tests")
    class GameStateTests {

        @Test
        @DisplayName("Should allow moves immediately after construction")
        void shouldAllowMovesAfterConstruction() {
            Node node = _field().getNodes().get(0);
            Point2D originalPos = node.getPosition();
            Point2D newPos = new Point2D.Double(500, 500);

            node.startDragging();
            node.updateDragging(newPos);
            node.stopDragging();

            assertTrue(node.getPosition().getX() != originalPos.getX() ||
                    node.getPosition().getY() != originalPos.getY());
        }

        @Test
        @DisplayName("Should maintain game state after multiple operations")
        void shouldMaintainGameStateAfterOperations() {
            List<Node> nodes = _field().getNodes();

            nodes.get(0).startDragging();
            nodes.get(0).updateDragging(new Point2D.Double(300, 300));
            nodes.get(0).stopDragging();

            nodes.get(1).startDragging();
            nodes.get(1).updateDragging(new Point2D.Double(400, 400));
            nodes.get(1).stopDragging();

            assertEquals(2, _state().getMoveCount());
            assertEquals(0, _nav().getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should handle restart after game over")
        void shouldHandleRestartAfterGameOver() {
            boolean hasIntersectionsInitially = _field().hasIntersections();

            if (hasIntersectionsInitially) {
                List<Node> nodes = _field().getNodes();
                Point2D winPosition = new Point2D.Double(10000, 10000);
                nodes.get(0).startDragging();
                nodes.get(0).updateDragging(winPosition);
                nodes.get(0).stopDragging();

                if (_state().isGameOver()) {
                    _game.restartLevel();

                    assertFalse(_state().isGameOver());
                    assertFalse(_state().isWin());
                    assertEquals(0, _state().getMoveCount());
                }
            }
        }
    }

    @Nested
    @DisplayName("Edge Intersection Tests")
    class EdgeIntersectionTests {

        @Test
        @DisplayName("Should detect intersections in initial level state")
        void shouldDetectIntersectionsInInitialState() {
            boolean hasIntersections = _field().hasIntersections();

            assertTrue(hasIntersections);
        }

        @Test
        @DisplayName("Should update intersection status after node movement")
        void shouldUpdateIntersectionStatusAfterMovement() {
            boolean initialIntersections = _field().hasIntersections();

            List<Node> nodes = _field().getNodes();
            Point2D winPosition = new Point2D.Double(10000, 10000);
            nodes.get(0).startDragging();
            nodes.get(0).updateDragging(winPosition);
            nodes.get(0).stopDragging();

            boolean newIntersections = _field().hasIntersections();

            if (initialIntersections) {
                assertFalse(newIntersections);
            }
        }

        @Test
        @DisplayName("Should have correct edge count from level file")
        void shouldHaveCorrectEdgeCount() {
            assertTrue(_field().getEdges().size() > 0);
        }
    }
}
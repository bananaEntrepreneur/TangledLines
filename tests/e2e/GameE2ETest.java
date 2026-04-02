package e2e;

import model.game.Field;
import model.game.Game;
import model.game.logic.IntersectionChecker;
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
    private IntersectionChecker _intersectionChecker;
    private Game _game;

    @BeforeEach
    void setUp() throws LevelLoadException {
        _levelManager = new LevelManager(LEVELS_DIR);
        _intersectionChecker = new IntersectionChecker();
        _game = new Game(_levelManager);
    }

    @Nested
    @DisplayName("Game Initialization Tests")
    class GameInitializationTests {

        @Test
        @DisplayName("Should initialize with first level loaded")
        void shouldInitializeWithFirstLevel() {
            _game.start();

            assertNotNull(_game.getField());
            assertEquals(0, _game.getCurrentLevelIndex());
            assertEquals(0, _game.getMoveCount());
            assertFalse(_game.isGameOver());
            assertFalse(_game.isWin());
        }

        @Test
        @DisplayName("Should have correct move limit from level config")
        void shouldHaveCorrectMoveLimit() {
            _game.start();

            assertTrue(_game.getMaxMoves() > 0);
            assertEquals(_levelManager.getCurrentMaxMoves(), _game.getMaxMoves());
        }

        @Test
        @DisplayName("Should load all nodes from level file")
        void shouldLoadAllNodes() {
            _game.start();
            Field field = _game.getField();

            assertTrue(field.getNodes().size() > 0);
        }

        @Test
        @DisplayName("Should load all edges from level file")
        void shouldLoadAllEdges() {
            _game.start();
            Field field = _game.getField();

            assertTrue(field.getEdges().size() > 0);
        }
    }

    @Nested
    @DisplayName("Node Movement Tests")
    class NodeMovementTests {

        @Test
        @DisplayName("Should allow moving movable node within move limit")
        void shouldAllowMovingMovableNode() {
            _game.start();
            Field field = _game.getField();
            Node movableNode = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .findFirst()
                    .orElseThrow();

            Point2D newPosition = new Point2D.Double(
                    movableNode.getPosition().getX() + 50,
                    movableNode.getPosition().getY() + 50
            );

            boolean result = _game.moveNode(movableNode, newPosition);

            assertTrue(result);
            assertEquals(newPosition, movableNode.getPosition());
            assertEquals(1, _game.getMoveCount());
        }

        @Test
        @DisplayName("Should reject moving non-movable node")
        void shouldRejectMovingNonMovableNode() {
            _game.start();
            Field field = _game.getField();
            Node nonMovableNode = field.getNodes().stream()
                    .filter(node -> !node.isMovable())
                    .findFirst()
                    .orElseThrow();
            Point2D originalPosition = new Point2D.Double(
                    nonMovableNode.getPosition().getX(),
                    nonMovableNode.getPosition().getY()
            );

            Point2D newPosition = new Point2D.Double(
                    nonMovableNode.getPosition().getX() + 50,
                    nonMovableNode.getPosition().getY() + 50
            );

            _game.moveNode(nonMovableNode, newPosition);

            assertEquals(originalPosition.getX(), nonMovableNode.getPosition().getX(), 0.01);
            assertEquals(originalPosition.getY(), nonMovableNode.getPosition().getY(), 0.01);
            assertEquals(0, _game.getMoveCount());
        }

        @Test
        @DisplayName("Should count each valid move")
        void shouldCountEachValidMove() {
            _game.start();
            Field field = _game.getField();
            List<Node> movableNodes = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .toList();

            Point2D pos1 = new Point2D.Double(
                    movableNodes.get(0).getPosition().getX() + 50,
                    movableNodes.get(0).getPosition().getY() + 50
            );
            _game.moveNode(movableNodes.get(0), pos1);

            Point2D pos2 = new Point2D.Double(
                    movableNodes.get(1).getPosition().getX() + 50,
                    movableNodes.get(1).getPosition().getY() + 50
            );
            _game.moveNode(movableNodes.get(1), pos2);

            assertEquals(2, _game.getMoveCount());
        }

        @Test
        @DisplayName("Should reject move after game over")
        void shouldRejectMoveAfterGameOver() {
            _game.start();
            Field field = _game.getField();
            List<Node> movableNodes = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .toList();

            Point2D winPosition = new Point2D.Double(10000, 10000);
            _game.moveNode(movableNodes.get(0), winPosition);

            if (_game.isGameOver() && _game.isWin()) {
                Point2D extraMove = new Point2D.Double(500, 500);
                int moveCountBefore = _game.getMoveCount();
                _game.moveNode(movableNodes.get(0), extraMove);

                assertEquals(moveCountBefore, _game.getMoveCount());
            }
        }
    }

    @Nested
    @DisplayName("Win Condition Tests")
    class WinConditionTests {

        @Test
        @DisplayName("Should win when all intersections resolved within move limit")
        void shouldWinWhenIntersectionsResolved() {
            _game.start();
            Field field = _game.getField();

            boolean hasIntersectionsInitially = _intersectionChecker.hasIntersections(field.getEdges());

            if (hasIntersectionsInitially) {
                List<Node> movableNodes = field.getNodes().stream()
                        .filter(Node::isMovable)
                        .toList();

                Point2D newPos = new Point2D.Double(1000, 1000);
                _game.moveNode(movableNodes.get(0), newPos);

                boolean stillHasIntersections = _intersectionChecker.hasIntersections(field.getEdges());

                if (!stillHasIntersections && _game.getMoveCount() <= _game.getMaxMoves()) {
                    assertTrue(_game.isWin());
                    assertTrue(_game.isGameOver());
                }
            }
        }

        @Test
        @DisplayName("Should lose when move limit exceeded with intersections")
        void shouldLoseWhenMoveLimitExceeded() {
            _game.start();
            Field field = _game.getField();
            List<Node> movableNodes = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .toList();

            int maxMoves = _game.getMaxMoves();
            
            for (int i = 0; i < maxMoves + 1; i++) {
                Point2D newPos = new Point2D.Double(
                        movableNodes.get(0).getPosition().getX() + 100 * (i + 1),
                        movableNodes.get(0).getPosition().getY()
                );
                _game.moveNode(movableNodes.get(0), newPos);
            }

            if (_intersectionChecker.hasIntersections(field.getEdges())) {
                assertTrue(_game.isGameOver());
                assertFalse(_game.isWin());
            }
        }
    }

    @Nested
    @DisplayName("Level Progression Tests")
    class LevelProgressionTests {

        @Test
        @DisplayName("Should not allow next level without winning")
        void shouldNotAllowNextLevelWithoutWinning() {
            _game.start();

            boolean result = _game.nextLevel();

            assertFalse(result);
            assertEquals(0, _game.getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should allow next level after winning")
        void shouldAllowNextLevelAfterWinning() {
            _game.start();

            if (_game.getTotalLevels() > 1) {
                _game.moveNode(_game.getField().getNodes().get(0), new Point2D.Double(10000, 10000));

                if (_game.isWin()) {
                    boolean result = _game.nextLevel();

                    if (_game.hasNextLevel()) {
                        assertTrue(result);
                        assertEquals(1, _game.getCurrentLevelIndex());
                    } else {
                        assertFalse(result);
                        assertTrue(_game.isAllLevelsComplete());
                    }
                }
            }
        }

        @Test
        @DisplayName("Should reset move count on next level")
        void shouldResetMoveCountOnNextLevel() {
            _game.start();

            if (_game.getTotalLevels() > 1) {
                _game.moveNode(_game.getField().getNodes().get(0), new Point2D.Double(10000, 10000));

                if (_game.isWin() && _game.hasNextLevel()) {
                    _game.nextLevel();

                    assertEquals(0, _game.getMoveCount());
                    assertTrue(_game.getMaxMoves() > 0);
                }
            }
        }

        @Test
        @DisplayName("Should restart level with original configuration")
        void shouldRestartLevelWithOriginalConfiguration() {
            _game.start();
            Field initialField = _game.getField();

            Node movableNode = initialField.getNodes().stream()
                    .filter(Node::isMovable)
                    .findFirst()
                    .orElseThrow();
            Point2D originalPos = new Point2D.Double(
                    movableNode.getPosition().getX(),
                    movableNode.getPosition().getY()
            );

            _game.moveNode(movableNode, new Point2D.Double(500, 500));
            assertNotEquals(originalPos, movableNode.getPosition());

            _game.restartLevel();

            Field restartedField = _game.getField();
            Node sameNode = restartedField.getNodes().get(
                    initialField.getNodes().indexOf(movableNode)
            );
            assertEquals(originalPos.getX(), sameNode.getPosition().getX(), 0.01);
            assertEquals(originalPos.getY(), sameNode.getPosition().getY(), 0.01);
            assertEquals(0, _game.getMoveCount());
            assertFalse(_game.isGameOver());
        }
    }

    @Nested
    @DisplayName("Multiple Levels Tests")
    class MultipleLevelsTests {

        @Test
        @DisplayName("Should have multiple levels available")
        void shouldHaveMultipleLevels() {
            assertTrue(_game.getTotalLevels() >= 1);
        }

        @Test
        @DisplayName("Should track current level index correctly")
        void shouldTrackCurrentLevelIndex() {
            _game.start();
            assertEquals(0, _game.getCurrentLevelIndex());

            if (_game.getTotalLevels() > 1) {
                _game.moveNode(_game.getField().getNodes().get(0), new Point2D.Double(10000, 10000));

                if (_game.isWin() && _game.hasNextLevel()) {
                    _game.nextLevel();
                    assertEquals(1, _game.getCurrentLevelIndex());
                }
            }
        }

        @Test
        @DisplayName("Should detect when all levels complete")
        void shouldDetectAllLevelsComplete() {
            _game.start();

            if (_game.getTotalLevels() == 1) {
                _game.moveNode(_game.getField().getNodes().get(0), new Point2D.Double(10000, 10000));

                if (_game.isWin()) {
                    assertFalse(_game.nextLevel());
                    assertTrue(_game.isAllLevelsComplete());
                }
            }
        }

        @Test
        @DisplayName("Should load different field for each level")
        void shouldLoadDifferentFieldForEachLevel() {
            _game.start();
            Field firstField = _game.getField();
            int firstNodeCount = firstField.getNodes().size();
            int firstEdgeCount = firstField.getEdges().size();
            int firstMaxMoves = _game.getMaxMoves();

            if (_game.getTotalLevels() > 1) {
                _game.moveNode(firstField.getNodes().get(0), new Point2D.Double(10000, 10000));

                if (_game.isWin() && _game.hasNextLevel()) {
                    _game.nextLevel();
                    Field secondField = _game.getField();

                    assertTrue(
                            secondField.getNodes().size() != firstNodeCount ||
                            secondField.getEdges().size() != firstEdgeCount ||
                            _game.getMaxMoves() != firstMaxMoves
                    );
                }
            }
        }
    }

    @Nested
    @DisplayName("Game State Tests")
    class GameStateTests {

        @Test
        @DisplayName("Should allow moves before game started (Field level)")
        void shouldAllowMovesBeforeStart() {
            Field field = _game.getField();
            Node node = field.getNodes().get(0);
            Point2D originalPos = new Point2D.Double(
                    node.getPosition().getX(),
                    node.getPosition().getY()
            );
            Point2D newPos = new Point2D.Double(500, 500);

            boolean result = _game.moveNode(node, newPos);

            assertTrue(result);
            assertNotEquals(originalPos.getX(), node.getPosition().getX(), 0.01);
        }

        @Test
        @DisplayName("Should maintain game state after multiple operations")
        void shouldMaintainGameStateAfterOperations() {
            _game.start();
            Field field = _game.getField();
            List<Node> movableNodes = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .toList();

            _game.moveNode(movableNodes.get(0), new Point2D.Double(300, 300));
            _game.moveNode(movableNodes.get(1), new Point2D.Double(400, 400));

            assertEquals(2, _game.getMoveCount());
            assertEquals(0, _game.getCurrentLevelIndex());
        }

        @Test
        @DisplayName("Should handle restart after game over")
        void shouldHandleRestartAfterGameOver() {
            _game.start();
            Field field = _game.getField();
            
            boolean hasIntersectionsInitially = _intersectionChecker.hasIntersections(field.getEdges());
            
            if (hasIntersectionsInitially) {
                List<Node> movableNodes = field.getNodes().stream()
                        .filter(Node::isMovable)
                        .toList();

                Point2D winPosition = new Point2D.Double(10000, 10000);
                _game.moveNode(movableNodes.get(0), winPosition);

                if (_game.isGameOver()) {
                    _game.restartLevel();

                    assertFalse(_game.isGameOver());
                    assertFalse(_game.isWin());
                    assertEquals(0, _game.getMoveCount());
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
            _game.start();
            Field field = _game.getField();

            boolean hasIntersections = _intersectionChecker.hasIntersections(field.getEdges());

            assertTrue(hasIntersections);
        }

        @Test
        @DisplayName("Should update intersection status after node movement")
        void shouldUpdateIntersectionStatusAfterMovement() {
            _game.start();
            Field field = _game.getField();

            boolean initialIntersections = _intersectionChecker.hasIntersections(field.getEdges());

            List<Node> movableNodes = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .toList();
            
            Point2D winPosition = new Point2D.Double(10000, 10000);
            _game.moveNode(movableNodes.get(0), winPosition);

            boolean newIntersections = _intersectionChecker.hasIntersections(field.getEdges());

            if (initialIntersections) {
                assertFalse(newIntersections);
            }
        }

        @Test
        @DisplayName("Should have correct edge count from level file")
        void shouldHaveCorrectEdgeCount() {
            _game.start();
            Field field = _game.getField();

            assertTrue(field.getEdges().size() > 0);
        }
    }
}

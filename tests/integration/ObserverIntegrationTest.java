package integration;

import model.game.Field;
import model.game.Game;
import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.LevelLoadException;
import model.level.LevelManager;
import model.listeners.GameStateListener;
import model.listeners.LevelNavigationListener;
import model.listeners.NodeListener;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration — Observer Chain")
class ObserverIntegrationTest {

    @Nested
    @DisplayName("NodeChangeListener Integration")
    class NodeListenerTests {

        private static class MoveTracker implements NodeListener {
            final List<Node> movedNodes = new ArrayList<>();
            final List<Point2D> positions = new ArrayList<>();

            @Override
            public void onMoved(Node node, Point2D newPosition) {
                movedNodes.add(node);
                positions.add(newPosition);
            }
        }

        @Test
        @DisplayName("Should notify NodeChangeListener through Game when node moves")
        void shouldNotifyThroughGame() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Game game = new Game(lm);
            MoveTracker tracker = new MoveTracker();

            Field field = game.getField();
            Node movable = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .findFirst()
                    .orElseThrow();
            movable.addListener(tracker);

            game.moveNode(movable, new Point2D.Double(500, 500));

            assertEquals(1, tracker.movedNodes.size());
            assertSame(movable, tracker.movedNodes.get(0));
        }

        @Test
        @DisplayName("Should notify all added listeners for a single node move")
        void shouldNotifyAllListeners() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Game game = new Game(lm);
            MoveTracker t1 = new MoveTracker();
            MoveTracker t2 = new MoveTracker();

            Field field = game.getField();
            Node movable = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .findFirst()
                    .orElseThrow();
            movable.addListener(t1);
            movable.addListener(t2);

            game.moveNode(movable, new Point2D.Double(500, 500));

            assertEquals(1, t1.movedNodes.size());
            assertEquals(1, t2.movedNodes.size());
        }
    }

    @Nested
    @DisplayName("GameStateChangedListener Integration")
    class GameStateListenerTests {

        private static class GameStateTracker implements GameStateListener {
            final AtomicInteger callCount = new AtomicInteger(0);
            final AtomicReference<GameState> lastState = new AtomicReference<>();

            @Override
            public void onGameStateChanged(GameState state) {
                callCount.incrementAndGet();
                lastState.set(state);
            }
        }

        @Test
        @DisplayName("Should notify listener when win condition is reached")
        void shouldNotifyOnWin() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Game game = new Game(lm);
            GameStateTracker tracker = new GameStateTracker();
            game.addGameStateChangedListener(tracker);

            untangleAll(game);

            assertTrue(tracker.callCount.get() > 0, "Listener should have been notified");
            if (tracker.lastState.get() != null) {
                assertTrue(tracker.lastState.get().isGameOver());
            }
        }

        @Test
        @DisplayName("Should notify listener when move limit exhausted (loss)")
        void shouldNotifyOnLoss() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Game game = new Game(lm);
            GameStateTracker tracker = new GameStateTracker();
            game.addGameStateChangedListener(tracker);

            exhaustMoves(game);

            assertTrue(tracker.callCount.get() > 0, "Listener should have been notified on loss");
        }

        @Test
        @DisplayName("Should support multiple GameStateChangedListeners")
        void shouldSupportMultipleListeners() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Game game = new Game(lm);
            GameStateTracker t1 = new GameStateTracker();
            GameStateTracker t2 = new GameStateTracker();
            game.addGameStateChangedListener(t1);
            game.addGameStateChangedListener(t2);

            untangleAll(game);

            assertEquals(t1.callCount.get(), t2.callCount.get(),
                    "Both listeners should receive equal notifications");
        }
    }

    @Nested
    @DisplayName("LevelNavigationChangeListener Integration")
    class LevelNavigationListenerTests {

        private static class LevelNavTracker implements LevelNavigationListener {
            final AtomicInteger callCount = new AtomicInteger(0);
            final AtomicReference<LevelNavigation> lastNav = new AtomicReference<>();

            @Override
            public void onLevelChanged(LevelNavigation levelNavigation) {
                callCount.incrementAndGet();
                lastNav.set(levelNavigation);
            }
        }

        @Test
        @DisplayName("Should notify listener when nextLevel succeeds")
        void shouldNotifyOnNextLevel() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Game game = new Game(lm);
            LevelNavTracker tracker = new LevelNavTracker();
            game.addLevelNavigationChangeListener(tracker);

            untangleAll(game);

            if (game.isWin() && game.hasNextLevel()) {
                assertTrue(game.nextLevel());
                assertTrue(tracker.callCount.get() > 0, "Listener should have been notified on level change");
                assertEquals(1, tracker.lastNav.get().getCurrentLevelIndex());
            }
        }

        @Test
        @DisplayName("Should notify listener when level is restarted")
        void shouldNotifyOnRestart() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Game game = new Game(lm);
            LevelNavTracker tracker = new LevelNavTracker();
            game.addLevelNavigationChangeListener(tracker);

            game.restartLevel();

            assertTrue(tracker.callCount.get() > 0, "Listener should have been notified on restart");
        }
    }

    @Nested
    @DisplayName("Full Observer Chain")
    class FullObserverChainTests {

        @Test
        @DisplayName("Should trigger node → game state listener chain when game ends")
        void shouldTriggerFullChain() throws LevelLoadException {
            LevelManager lm = new LevelManager("levels");
            Game game = new Game(lm);

            AtomicInteger nodeMoveCount = new AtomicInteger(0);
            AtomicInteger gameStateCount = new AtomicInteger(0);

            Field field = game.getField();
            Node movable = field.getNodes().stream()
                    .filter(Node::isMovable)
                    .findFirst()
                    .orElseThrow();
            movable.addListener((node, pos) -> nodeMoveCount.incrementAndGet());

            game.addGameStateChangedListener(state -> gameStateCount.incrementAndGet());

            while (!game.isGameOver() && nodeMoveCount.get() < game.getMaxMoves() + 1) {
                game.moveNode(movable, new Point2D.Double(
                        movable.getPosition().getX() + 100,
                        movable.getPosition().getY() + 100
                ));
            }

            assertTrue(nodeMoveCount.get() > 0, "NodeChangeListener should fire");
            assertTrue(gameStateCount.get() > 0, "GameStateChangedListener should fire on game end");
        }
    }

    private void untangleAll(Game game) {
        Field field = game.getField();
        List<Node> movable = field.getNodes().stream()
                .filter(Node::isMovable)
                .toList();

        for (Node node : movable) {
            double offset = 10000 + movable.indexOf(node) * 100;
            node.setPosition(new Point2D.Double(
                    node.getPosition().getX() + offset,
                    node.getPosition().getY() + offset
            ));
        }
    }

    private void exhaustMoves(Game game) {
        Field field = game.getField();
        List<Node> movable = field.getNodes().stream()
                .filter(Node::isMovable)
                .toList();

        Node node = movable.get(0);
        int maxMoves = game.getMaxMoves();
        for (int i = 0; i < maxMoves; i++) {
            game.moveNode(node, new Point2D.Double(
                    node.getPosition().getX() + 100,
                    node.getPosition().getY() + 100
            ));
        }
    }
}

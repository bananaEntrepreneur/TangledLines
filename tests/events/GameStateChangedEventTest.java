package events;

import model.game.Field;
import model.game.Game;
import model.game.GameState;
import model.game.LevelNavigation;
import model.level.Level;
import model.level.LevelManager;
import model.level.seeder.Seeder;
import model.listeners.GameStateListener;
import model.listeners.Priority;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GameStateListener.onGameStateChanged Event Tests")
class GameStateChangedEventTest {

    @Test
    @DisplayName("Should call game state listeners in order after move count changes")
    void shouldCallGameStateListenersInOrderAfterMoveCountChanges() {
        Game game = new Game(new LevelManager(new CrossingSeeder()));
        GameState state = game.getState();
        Node node = state.getField().getNodes().get(0);
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(changedState -> events.add("first:" + describe(changedState))));
        state.addListener(gameStateListener(changedState -> events.add("second:" + describe(changedState))));

        drag(node, 10, 0);

        assertEquals(List.of(
            "first:maxMoves=5 moves=1 finished=false",
            "second:maxMoves=5 moves=1 finished=false"
        ), events);
    }

    @Test
    @DisplayName("Should call game state listeners in order after level state changes")
    void shouldCallGameStateListenersInOrderAfterLevelStateChanges() {
        Game game = new Game(new LevelManager(new CrossingSeeder()));
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(changedState -> events.add(
            "first:field=true " + describe(changedState)
        )));
        state.addListener(gameStateListener(changedState -> events.add(
            "second:field=true " + describe(changedState)
        )));

        drag(state.getField().getNodes().get(0), 10, 0);
        events.clear();

        navigation.restartLevel();

        assertEquals(List.of(
            "first:field=true maxMoves=5 moves=0 finished=false",
            "second:field=true maxMoves=5 moves=0 finished=false"
        ), events);
    }

    @Test
    @DisplayName("Should notify each unique game state listener once per state change")
    void shouldNotifyEachUniqueGameStateListenerOncePerStateChange() {
        Game game = new Game(new LevelManager(new CrossingSeeder()));
        GameState state = game.getState();
        AtomicInteger notificationCount = new AtomicInteger();
        GameStateListener listener = gameStateListener(changedState -> notificationCount.incrementAndGet());
        state.addListener(listener);
        state.addListener(listener);

        drag(state.getField().getNodes().get(0), 10, 0);

        assertEquals(1, notificationCount.get());
    }

    @Test
    @DisplayName("Should notify listeners after level result and all-levels-complete transitions")
    void shouldNotifyListenersAfterTerminalStateTransitions() {
        Game game = new Game(new LevelManager(new WinAndFinishSeeder()));
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(changedState -> events.add(describeTerminalState(changedState))));

        drag(state.getField().getNodes().get(0), 10, 0);
        navigation.restartLevel();
        drag(state.getField().getNodes().get(1), 0, -200);
        navigation.nextLevel();
        drag(state.getField().getNodes().get(0), 50, 0);
        navigation.nextLevel();

        assertEquals(List.of(
            "finished=false won=false allComplete=false",
            "finished=false won=false allComplete=false",
            "finished=false won=false allComplete=false",
            "finished=true won=true allComplete=false",
            "finished=false won=false allComplete=false",
            "finished=false won=false allComplete=false",
            "finished=true won=true allComplete=false",
            "finished=false won=false allComplete=true"
        ), events);
    }

    @Test
    @DisplayName("Should not overwrite the current level result after it is finished")
    void shouldNotOverwriteCurrentLevelResultAfterItIsFinished() {
        Game game = new Game(new LevelManager(new WinAndFinishSeeder()));
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(changedState -> events.add(describeTerminalState(changedState))));

        drag(state.getField().getNodes().get(1), 0, -200);
        events.clear();

        navigation.nextLevel();
        events.clear();

        drag(state.getField().getNodes().get(0), 10, 0);
        events.clear();

        drag(state.getField().getNodes().get(0), 20, 0);

        assertEquals(List.of(), events);
        assertTrue(state.isCurrentLevelWon());
    }

    @Test
    @DisplayName("Should pass the changed game state instance to listeners")
    void shouldPassChangedGameStateInstanceToListeners() {
        Game game = new Game(new LevelManager(new CrossingSeeder()));
        GameState state = game.getState();
        Node node = state.getField().getNodes().get(0);
        List<GameState> notifiedStates = new ArrayList<>();

        state.addListener(gameStateListener(notifiedStates::add));

        drag(node, 10, 0);

        assertEquals(1, notifiedStates.size());
        assertSame(state, notifiedStates.get(0));
    }

    @Test
    @DisplayName("Should reset terminal status when loading a level")
    void shouldResetTerminalStatusWhenLoadingLevel() {
        Game game = new Game(new LevelManager(new WinAndFinishSeeder()));
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(changedState -> events.add(
            describeTerminalState(changedState) + " moves=" + changedState.getMoveCount()
        )));

        drag(state.getField().getNodes().get(2), 0, -100);
        events.clear();

        navigation.restartLevel();

        assertEquals(List.of("finished=false won=false allComplete=false moves=0"), events);
    }

    private String describe(GameState state) {
        return String.format(
            "maxMoves=%d moves=%d finished=%s",
            state.getMaxMoveCount(),
            state.getMoveCount(),
            state.isCurrentLevelFinished()
        );
    }

    private String describeTerminalState(GameState state) {
        return String.format(
            "finished=%s won=%s allComplete=%s",
            state.isCurrentLevelFinished(),
            state.isCurrentLevelWon(),
            state.isAllLevelsComplete()
        );
    }

    private GameStateListener gameStateListener(Consumer<GameState> handler) {
        return new GameStateListener() {
            @Override
            public void onGameStateChanged(GameState gameState) {
                handler.accept(gameState);
            }

            @Override
            public Priority getPriority() {
                return Priority.MEDIUM;
            }
        };
    }

    private void drag(Node node, double x, double y) {
        node.startDragging();
        node.updateDragging(new Point2D.Double(x, y));
        node.stopDragging();
    }

    private static class CrossingSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(createLevel(5, () -> {
                Field field = createField();
                Node a = createNode(field, 0, 0);
                Node b = createNode(field, 100, 100);
                Node c = createNode(field, 0, 100);
                Node d = createNode(field, 100, 0);
                createEdge(field, a, b);
                createEdge(field, c, d);
                return field;
            }));
        }
    }

    private static class OneLevelSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(createLevel(5, () -> {
                Field field = createField();
                createEdge(field, createNode(field, 0, 0), createNode(field, 100, 0));
                return field;
            }));
        }
    }

    private static class WinAndFinishSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(
                createLevel(2, () -> {
                    Field field = createField();
                    Node a = createNode(field, 0, 0);
                    Node b = createNode(field, 100, 100);
                    Node c = createNode(field, 0, 100);
                    Node d = createNode(field, 100, 0);
                    createEdge(field, a, b);
                    createEdge(field, c, d);
                    return field;
                }),
                createLevel(1, () -> {
                    Field field = createField();
                    createEdge(field, createNode(field, 0, 0), createNode(field, 100, 0));
                    return field;
                })
            );
        }
    }
}

package events;

import model.game.Field;
import model.game.Game;
import model.game.GameState;
import model.game.LevelNavigation;
import model.level.Level;
import model.level.LevelManager;
import model.level.seeder.Seeder;
import model.listeners.GameStateListener;
import model.listeners.LevelNavigationListener;
import model.listeners.ListenerPriority;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Point2D;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LevelNavigationListener.onLevelChanged Event Tests")
class LevelChangedEventTest {

    @Test
    @DisplayName("Should call level navigation listeners in order after state changes")
    void shouldCallLevelNavigationListenersInOrderAfterStateChanges() throws Exception {
        Game game = new Game(singleLevelManager());
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(gameState -> events.add("state:maxMoves=" + gameState.getMaxMoveCount())));
        navigation.addListener(levelNavigationListener(levelNavigation -> events.add(
            "first:index=" + levelNavigation.getCurrentLevelIndex() + " maxMoves=" + state.getMaxMoveCount()
        )));
        navigation.addListener(levelNavigationListener(levelNavigation -> events.add(
            "second:index=" + levelNavigation.getCurrentLevelIndex() + " maxMoves=" + state.getMaxMoveCount()
        )));

        navigation.restartLevel();

        assertEquals(List.of(
            "state:maxMoves=3",
            "first:index=0 maxMoves=3",
            "second:index=0 maxMoves=3"
        ), events);
    }

    @Test
    @DisplayName("Should notify each unique level navigation listener once per level change")
    void shouldNotifyEachUniqueLevelNavigationListenerOncePerLevelChange() throws Exception {
        Game game = new Game(singleLevelManager());
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        AtomicInteger notificationCount = new AtomicInteger();
        LevelNavigationListener listener = levelNavigationListener(
            changedNavigation -> notificationCount.incrementAndGet()
        );

        navigation.addListener(null);
        navigation.addListener(listener);
        navigation.addListener(listener);

        navigation.restartLevel();

        assertEquals(1, notificationCount.get());
    }

    @Test
    @DisplayName("Should pass the changed level navigation instance to listeners")
    void shouldPassChangedLevelNavigationInstanceToListeners() throws Exception {
        Game game = new Game(singleLevelManager());
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<LevelNavigation> notifiedNavigations = new ArrayList<>();

        navigation.addListener(levelNavigationListener(notifiedNavigations::add));

        navigation.restartLevel();

        assertEquals(1, notifiedNavigations.size());
        assertSame(navigation, notifiedNavigations.get(0));
    }

    @Test
    @DisplayName("Should notify state listeners before level listeners when moving to next level")
    void shouldNotifyStateListenersBeforeLevelListenersWhenMovingToNextLevel() throws Exception {
        Game game = new Game(twoLevelManager());
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(gameState -> events.add(
            "state:maxMoves=" + gameState.getMaxMoveCount() + " index=" + navigation.getCurrentLevelIndex()
        )));
        navigation.addListener(levelNavigationListener(levelNavigation -> events.add(
            "level:index=" + levelNavigation.getCurrentLevelIndex() + " maxMoves=" + state.getMaxMoveCount()
        )));

        winCrossingLevel(state);
        events.clear();

        assertTrue(navigation.nextLevel());

        assertEquals(List.of(
            "state:maxMoves=7 index=1",
            "level:index=1 maxMoves=7"
        ), events);
    }

    @Test
    @DisplayName("Should not notify level listeners when next level is blocked")
    void shouldNotNotifyLevelListenersWhenNextLevelIsBlocked() throws Exception {
        Game game = new Game(twoLevelManager());
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(gameState -> events.add("state")));
        navigation.addListener(levelNavigationListener(levelNavigation -> events.add("level")));

        assertFalse(navigation.nextLevel());

        assertEquals(List.of(), events);
        assertEquals(0, navigation.getCurrentLevelIndex());
    }

    @Test
    @DisplayName("Should complete all levels without firing a level changed event")
    void shouldCompleteAllLevelsWithoutFiringLevelChangedEvent() throws Exception {
        Game game = new Game(singleLevelManager());
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(
            gameState -> events.add("state:allComplete=" + gameState.isAllLevelsComplete())
        ));
        navigation.addListener(levelNavigationListener(levelNavigation -> events.add("level")));

        winSingleLevel(state);
        events.clear();

        assertFalse(navigation.nextLevel());

        assertEquals(List.of("state:allComplete=true"), events);
        assertTrue(state.isAllLevelsComplete());
    }

    private LevelManager singleLevelManager() {
        return new LevelManager(new SingleLevelSeeder());
    }

    private LevelManager twoLevelManager() {
        return new LevelManager(new NumberedLevelSeeder());
    }

    private void winCrossingLevel(GameState state) {
        Node node = state.getField().getNodes().get(2);
        drag(node, 0, -100);
    }

    private void winSingleLevel(GameState state) {
        Node node = state.getField().getNodes().get(1);
        drag(node, 0, -200);
    }

    private GameStateListener gameStateListener(Consumer<GameState> handler) {
        return new GameStateListener() {
            @Override
            public void onGameStateChanged(GameState gameState) {
                handler.accept(gameState);
            }

            @Override
            public ListenerPriority getPriority() {
                return ListenerPriority.MEDIUM;
            }
        };
    }

    private LevelNavigationListener levelNavigationListener(Consumer<LevelNavigation> handler) {
        return new LevelNavigationListener() {
            @Override
            public void onLevelChanged(LevelNavigation levelNavigation) {
                handler.accept(levelNavigation);
            }

            @Override
            public ListenerPriority getPriority() {
                return ListenerPriority.MEDIUM;
            }
        };
    }

    private static class SingleLevelSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(crossingLevel(3));
        }

        protected Level crossingLevel(int maxMoves) {
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
    }

    private static class NumberedLevelSeeder extends SingleLevelSeeder {
        @Override
        public List<Level> seed() {
            return List.of(
                crossingLevel(3),
                createLevel(7, () -> {
                    Field field = createField();
                    createNode(field, 0, 0);
                    return field;
                })
            );
        }
    }

    private void drag(Node node, double x, double y) {
        node.startDragging();
        node.updateDragging(new Point2D.Double(x, y));
        node.stopDragging();
    }
}

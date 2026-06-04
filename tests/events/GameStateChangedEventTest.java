package events;

import model.game.Field;
import model.game.state.GameState;
import model.listeners.GameStateListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("GameStateListener.onGameStateChanged Event Tests")
class GameStateChangedEventTest {

    @Test
    @DisplayName("Should call game state listeners in order after move count changes")
    void shouldCallGameStateListenersInOrderAfterMoveCountChanges() {
        GameState state = new GameState(new Field(), 3);
        List<String> events = new ArrayList<>();

        state.addListener(changedState -> events.add("first:" + describe(changedState)));
        state.addListener(changedState -> events.add("second:" + describe(changedState)));

        state.incrementMoveCount();

        assertEquals(List.of(
            "first:maxMoves=3 moves=1 gameOver=false",
            "second:maxMoves=3 moves=1 gameOver=false"
        ), events);
    }

    @Test
    @DisplayName("Should call game state listeners in order after level state changes")
    void shouldCallGameStateListenersInOrderAfterLevelStateChanges() {
        GameState state = new GameState(new Field(), 3);
        Field nextField = new Field();
        List<String> events = new ArrayList<>();

        state.addListener(changedState -> events.add("first:field=" + (changedState.getField() == nextField)
            + " " + describe(changedState)));
        state.addListener(changedState -> events.add("second:field=" + (changedState.getField() == nextField)
            + " " + describe(changedState)));

        state.incrementMoveCount();
        events.clear();

        state.loadLevel(nextField, 5);

        assertEquals(List.of(
            "first:field=true maxMoves=5 moves=0 gameOver=false",
            "second:field=true maxMoves=5 moves=0 gameOver=false"
        ), events);
    }

    @Test
    @DisplayName("Should notify each unique game state listener once per state change")
    void shouldNotifyEachUniqueGameStateListenerOncePerStateChange() {
        GameState state = new GameState(new Field(), 3);
        AtomicInteger notificationCount = new AtomicInteger();
        GameStateListener listener = changedState -> notificationCount.incrementAndGet();

        state.addListener(null);
        state.addListener(listener);
        state.addListener(listener);

        state.incrementMoveCount();

        assertEquals(1, notificationCount.get());
    }

    @Test
    @DisplayName("Should notify listeners after win, lose, and all-levels-complete transitions")
    void shouldNotifyListenersAfterTerminalStateTransitions() {
        GameState state = new GameState(new Field(), 3);
        List<String> events = new ArrayList<>();

        state.addListener(changedState -> events.add(describeTerminalState(changedState)));

        state.win();
        state.lose();
        state.completeAllLevels();

        assertEquals(List.of(
            "gameOver=true win=true allComplete=false",
            "gameOver=true win=false allComplete=false",
            "gameOver=false win=false allComplete=true"
        ), events);
    }

    @Test
    @DisplayName("Should pass the changed game state instance to listeners")
    void shouldPassChangedGameStateInstanceToListeners() {
        GameState state = new GameState(new Field(), 3);
        List<GameState> notifiedStates = new ArrayList<>();

        state.addListener(notifiedStates::add);

        state.incrementMoveCount();

        assertEquals(1, notifiedStates.size());
        assertSame(state, notifiedStates.get(0));
    }

    @Test
    @DisplayName("Should reset terminal status when loading a level")
    void shouldResetTerminalStatusWhenLoadingLevel() {
        GameState state = new GameState(new Field(), 3);
        List<String> events = new ArrayList<>();

        state.addListener(changedState -> events.add(describeTerminalState(changedState)
            + " moves=" + changedState.getMoveCount()));

        state.incrementMoveCount();
        state.win();
        events.clear();

        state.loadLevel(new Field(), 5);

        assertEquals(List.of("gameOver=false win=false allComplete=false moves=0"), events);
    }

    private String describe(GameState state) {
        return String.format(
            "maxMoves=%d moves=%d gameOver=%s",
            state.getMaxMoves(),
            state.getMoveCount(),
            state.isGameOver()
        );
    }

    private String describeTerminalState(GameState state) {
        return String.format(
            "gameOver=%s win=%s allComplete=%s",
            state.isGameOver(),
            state.isWin(),
            state.isAllLevelsComplete()
        );
    }
}

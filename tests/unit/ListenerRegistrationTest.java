package unit;

import model.game.Field;
import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.Level;
import model.level.LevelManager;
import model.level.loader.LevelLoader;
import model.listeners.GameStateListener;
import model.listeners.LevelNavigationListener;
import model.listeners.NodeListener;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Listener Registration Tests")
class ListenerRegistrationTest {

    @Test
    @DisplayName("Should ignore duplicate and null node listeners")
    void shouldIgnoreDuplicateAndNullNodeListeners() {
        Node node = new Node(new Point2D.Double(0, 0));
        CountingNodeListener listener = new CountingNodeListener();

        node.addListener(null);
        node.addListener(listener);
        node.addListener(listener);

        node.startDragging();
        node.updateDragging(new Point2D.Double(10, 10));
        node.stopDragging();

        assertEquals(2, listener.count);
    }

    @Test
    @DisplayName("Should ignore duplicate and null game state listeners")
    void shouldIgnoreDuplicateAndNullGameStateListeners() {
        GameState state = new GameState(new Field(), 3);
        CountingGameStateListener listener = new CountingGameStateListener();

        state.addListener(null);
        state.addListener(listener);
        state.addListener(listener);

        state.incrementMoveCount();

        assertEquals(1, listener.count);
    }

    @Test
    @DisplayName("Should ignore duplicate and null level navigation listeners")
    void shouldIgnoreDuplicateAndNullLevelNavigationListeners() throws Exception {
        GameState state = new GameState(new Field(), 3);
        LevelNavigation navigation = new LevelNavigation(new LevelManager("levels", new SingleLevelLoader()), state);
        CountingLevelNavigationListener listener = new CountingLevelNavigationListener();

        navigation.addListener(null);
        navigation.addListener(listener);
        navigation.addListener(listener);

        navigation.restartLevel();

        assertEquals(1, listener.count);
    }

    private static class CountingNodeListener implements NodeListener {
        private int count = 0;

        @Override
        public void onMoved(Node node) {
            count++;
        }
    }

    private static class CountingGameStateListener implements GameStateListener {
        private int count = 0;

        @Override
        public void onGameStateChanged(GameState gameState) {
            count++;
        }
    }

    private static class CountingLevelNavigationListener implements LevelNavigationListener {
        private int count = 0;

        @Override
        public void onLevelChanged(LevelNavigation levelNavigation) {
            count++;
        }
    }

    private static class SingleLevelLoader implements LevelLoader {
        @Override
        public Level load(String filePath) throws IOException {
            return new Level(
                3,
                List.of(new Level.NodeData(0, 0)),
                List.of()
            );
        }
    }
}

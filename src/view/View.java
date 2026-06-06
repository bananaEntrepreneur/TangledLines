package view;

import model.game.GameState;
import model.game.LevelNavigation;
import model.listeners.GameStateListener;
import model.listeners.LevelNavigationListener;
import model.listeners.ListenerPriority;

public class View {
    private final GameFrame _frame;
    private final GameStateListener _gameStateListener = new GameStateListener() {
        @Override
        public void onGameStateChanged(GameState gameState) {
            _frame.refresh();
        }

        @Override
        public ListenerPriority getPriority() {
            return ListenerPriority.LOW;
        }
    };
    private final LevelNavigationListener _levelNavigationListener = new LevelNavigationListener() {
        @Override
        public void onLevelChanged(LevelNavigation levelNavigation) {
            _frame.recreateWidgets();
            _frame.refresh();
        }

        @Override
        public ListenerPriority getPriority() {
            return ListenerPriority.LOW;
        }
    };

    public View(
        GameState gameState,
        LevelNavigation navigation
    ) {
        _frame = new GameFrame(gameState, navigation);
        gameState.addListener(_gameStateListener);
        navigation.addListener(_levelNavigationListener);
    }

    public void show() {
        _frame.setVisible(true);
    }
}

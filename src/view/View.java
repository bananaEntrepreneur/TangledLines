package view;

import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.listeners.GameStateListener;
import model.listeners.LevelNavigationListener;

public class View implements GameStateListener, LevelNavigationListener {
    private final GameFrame _frame;

    public View(
        GameState gameState,
        LevelNavigation navigation
    ) {
        _frame = new GameFrame(gameState, navigation);
        gameState.addListener(this);
        navigation.addListener(this);
    }

    public void show() {
        _frame.setVisible(true);
    }

    @Override
    public void onGameStateChanged(GameState gameState) {
        _frame.refresh();
    }

    @Override
    public void onLevelChanged(LevelNavigation levelNavigation) {
        _frame.recreateWidgets();
        _frame.refresh();
    }
}

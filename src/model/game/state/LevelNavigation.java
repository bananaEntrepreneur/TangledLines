package model.game.state;

import model.game.Field;
import model.level.LevelManager;
import model.listeners.LevelNavigationListener;

import java.util.ArrayList;
import java.util.List;

public class LevelNavigation {
    private final LevelManager _levelManager;
    private final GameState _gameState;
    private final List<LevelNavigationListener> _listeners = new ArrayList<>();

    public LevelNavigation(LevelManager levelManager, GameState gameState) {
        _levelManager = levelManager;
        _gameState = gameState;
    }

    public void addListener(LevelNavigationListener listener) {
        if (listener != null && !_listeners.contains(listener)) {
            _listeners.add(listener);
            _listeners.sort((a, b) -> b.getPriority().getValue() - a.getPriority().getValue());
        }
    }

    public boolean nextLevel() {
        if (!_gameState.isGameOver() || !_gameState.isWin()) {
            return false;
        }

        Field nextField = _levelManager.nextField();
        if (nextField == null) {
            _gameState.completeAllLevels();
            return false;
        }

        _gameState.loadLevel(nextField, _levelManager.getCurrentMaxMoves());
        notifyLevelChanged();
        return true;
    }

    public void restartLevel() {
        Field currentField = _levelManager.getCurrentField();
        _gameState.loadLevel(currentField, _levelManager.getCurrentMaxMoves());
        notifyLevelChanged();
    }

    public int getCurrentLevelIndex() { return _levelManager.getCurrentLevelIndex(); }

    public int getTotalLevels() { return _levelManager.getTotalLevels(); }

    public boolean hasNextLevel() { return _levelManager.hasNextLevel(); }

    private void notifyLevelChanged() {
        for (LevelNavigationListener listener : _listeners) {
            listener.onLevelChanged(this);
        }
    }
}

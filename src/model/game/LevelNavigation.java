package model.game;

import model.level.LevelManager;
import model.listeners.LevelNavigationListener;

import java.util.ArrayList;
import java.util.List;

public class LevelNavigation {
    private final LevelManager _levelManager;
    private final GameState _gameState;
    private final List<LevelNavigationListener> _listeners = new ArrayList<>();

    LevelNavigation(LevelManager levelManager, GameState gameState) {
        if (levelManager == null) {
            throw new IllegalArgumentException("Level manager cannot be null");
        }
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null");
        }
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
        if (!_gameState.isCurrentLevelFinished() || !_gameState.isCurrentLevelWon()) {
            return false;
        }

        Field nextField = _levelManager.nextField();
        if (nextField == null) {
            _gameState.allLevelsComplete();
            return false;
        }

        _gameState.startLevel(nextField, _levelManager.getCurrentMaxMoveCount());
        notifyLevelChanged();
        return true;
    }

    public void restartLevel() {
        Field currentField = _levelManager.createCurrentField();
        _gameState.startLevel(currentField, _levelManager.getCurrentMaxMoveCount());
        notifyLevelChanged();
    }

    public int getCurrentLevelIndex() { return _levelManager.getCurrentLevelIndex(); }

    public int getTotalLevelCount() { return _levelManager.getTotalLevelCount(); }

    public boolean hasNextLevel() { return _levelManager.hasNextLevel(); }

    private void notifyLevelChanged() {
        for (LevelNavigationListener listener : _listeners) {
            listener.onLevelChanged(this);
        }
    }
}

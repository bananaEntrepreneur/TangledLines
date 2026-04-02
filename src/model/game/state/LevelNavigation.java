package model.game.state;

import model.game.Field;
import model.level.LevelManager;

public class LevelNavigation {
    private final LevelManager _levelManager;
    private final GameState _gameState;

    public LevelNavigation(LevelManager levelManager, GameState gameState) {
        _levelManager = levelManager;
        _gameState = gameState;
    }

    public boolean nextLevel() {
        if (!_gameState.isGameOver() || !_gameState.isWin()) {
            return false;
        }

        Field nextField = _levelManager.nextField();
        if (nextField == null) {
            _gameState.setAllLevelsComplete(true);
            return false;
        }

        _gameState.setField(nextField);
        _gameState.setMaxMoves(_levelManager.getCurrentMaxMoves());
        _gameState.reset();
        return true;
    }

    public void restartLevel() {
        Field currentField = _levelManager.getCurrentField();
        _gameState.setField(currentField);
        _gameState.setMaxMoves(_levelManager.getCurrentMaxMoves());
        _gameState.reset();
    }

    public int getCurrentLevelIndex() { return _levelManager.getCurrentLevelIndex(); }

    public int getTotalLevels() { return _levelManager.getTotalLevels(); }

    public boolean hasNextLevel() { return _levelManager.hasNextLevel(); }
}

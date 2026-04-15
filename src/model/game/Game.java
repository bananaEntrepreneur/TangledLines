package model.game;

import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.LevelManager;
import model.listeners.GameStateChangedListener;
import model.listeners.LevelNavigationChangeListener;
import model.listeners.NodeChangeListener;
import model.units.Node;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Game implements NodeChangeListener {
    private final GameState _gameState;
    private final LevelNavigation _levelNavigation;

    private final List<GameStateChangedListener> _gameStateListeners = new ArrayList<>();
    private final List<LevelNavigationChangeListener> _levelNavigationListeners = new ArrayList<>();

    public Game(LevelManager levelManager) {
        _gameState = new GameState(levelManager.getCurrentField(), levelManager.getCurrentMaxMoves());
        _levelNavigation = new LevelNavigation(levelManager, _gameState);
    }

    @Override
    public void onNodeMoved(Node node, Point2D newPosition) {
        if (_gameState.isGameOver() || _gameState.isAllLevelsComplete()) {
            return;
        }

        _gameState.incrementMoveCount();

        if (!_gameState.getField().hasIntersections()) {
            _gameState.setGameOver(true);
            _gameState.setWin(true);
        } else if (_gameState.getMoveCount() >= _gameState.getMaxMoves()) {
            _gameState.setGameOver(true);
            _gameState.setWin(false);
        } else {
            return;
        }
        notifyGameStateChangedListeners();
    }

    public void addGameStateChangedListener(GameStateChangedListener listener) {
        _gameStateListeners.add(listener);
    }

    public void addLevelNavigationChangeListener(LevelNavigationChangeListener listener) {
        _levelNavigationListeners.add(listener);
    }

    public boolean nextLevel() {
        boolean result = _levelNavigation.nextLevel();
        if (result) {
            notifyLevelNavigationChangeListeners();
            notifyGameStateChangedListeners();
        }
        return result;
    }

    public void restartLevel() {
        _levelNavigation.restartLevel();
        notifyLevelNavigationChangeListeners();
        notifyGameStateChangedListeners();
    }

    public Field getField() { return _gameState.getField(); }

    public int getCurrentLevelIndex() { return _levelNavigation.getCurrentLevelIndex(); }

    public int getTotalLevels() { return _levelNavigation.getTotalLevels(); }

    public boolean hasNextLevel() { return _levelNavigation.hasNextLevel(); }

    public boolean isWin() { return _gameState.isWin(); }

    public boolean isGameOver() { return _gameState.isGameOver(); }

    public boolean isAllLevelsComplete() { return _gameState.isAllLevelsComplete(); }

    public int getMoveCount() { return _gameState.getMoveCount(); }

    public int getMaxMoves() { return _gameState.getMaxMoves(); }

    private void notifyGameStateChangedListeners() {
        for (GameStateChangedListener listener : _gameStateListeners) {
            listener.onGameStateChanged(_gameState);
        }
    }

    private void notifyLevelNavigationChangeListeners() {
        for (LevelNavigationChangeListener listener : _levelNavigationListeners) {
            listener.onLevelChanged(_levelNavigation);
        }
    }
}

package model.game;

import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.LevelManager;
import model.listeners.GameStateListener;
import model.listeners.LevelNavigationListener;
import model.listeners.NodeListener;
import model.units.Node;

import java.util.ArrayList;
import java.util.List;

public class Game implements NodeListener {
    private final GameState _state;
    private final LevelNavigation _navigation;
    private final List<GameStateListener> _gameStateListeners = new ArrayList<>();
    private final List<LevelNavigationListener> _levelNavigationListeners = new ArrayList<>();

    public Game(LevelManager levelManager) {
        _state = new GameState(levelManager.getCurrentField(), levelManager.getCurrentMaxMoves());
        _navigation = new LevelNavigation(levelManager, _state);
        _state.getField().getNodes().forEach(node -> node.addListener(this));
    }

    public GameState getState() { return _state; }
    public LevelNavigation getNavigation() { return _navigation; }

    public void addGameStateListener(GameStateListener listener) { _gameStateListeners.add(listener); }
    public void addLevelNavigationListener(LevelNavigationListener listener) { _levelNavigationListeners.add(listener); }

    public boolean nextLevel() {
        boolean wasComplete = _state.isAllLevelsComplete();
        boolean result = _navigation.nextLevel();
        if (result || _state.isAllLevelsComplete() != wasComplete) {
            notifyLevelNavigation();
            notifyGameState();
        }
        return result;
    }

    public void restartLevel() {
        _navigation.restartLevel();
        notifyLevelNavigation();
        notifyGameState();
    }

    @Override
    public void onMoved(Node node) {
        if (_state.isGameOver() || _state.isAllLevelsComplete()) return;

        _state.incrementMoveCount();

        if (!_state.getField().hasIntersections()) {
            _state.setGameOver(true);
            _state.setWin(true);
        } else if (_state.getMoveCount() >= _state.getMaxMoves()) {
            _state.setGameOver(true);
            _state.setWin(false);
        } else {
            return;
        }
        notifyGameState();
    }

    private void notifyGameState() {
        for (GameStateListener l : _gameStateListeners) l.onGameStateChanged(_state);
    }

    private void notifyLevelNavigation() {
        for (LevelNavigationListener l : _levelNavigationListeners) l.onLevelChanged(_navigation);
    }
}
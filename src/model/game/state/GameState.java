package model.game.state;

import model.game.Field;
import model.listeners.GameStateListener;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Field _field;
    private int _maxMoves;
    private int _moveCount = 0;
    private boolean _gameOver = false;
    private boolean _win = false;
    private boolean _allLevelsComplete = false;
    private final List<GameStateListener> _listeners = new ArrayList<>();

    public GameState(Field field, int maxMoves) {
        _field = field;
        _maxMoves = maxMoves;
    }

    public void addListener(GameStateListener listener) {
        _listeners.add(listener);
    }

    public void loadLevel(Field field, int maxMoves) {
        _field = field;
        _maxMoves = maxMoves;
        _moveCount = 0;
        _gameOver = false;
        _win = false;
        _allLevelsComplete = false;
    }

    public void incrementMoveCount() {
        _moveCount++;
        notifyListeners();
    }

    public void setGameOver(boolean gameOver, boolean win) {
        _gameOver = gameOver;
        _win = win;
        notifyListeners();
    }

    public void setAllLevelsComplete(boolean allLevelsComplete) {
        _allLevelsComplete = allLevelsComplete;
        notifyListeners();
    }

    public boolean isGameOver() { return _gameOver; }

    public boolean isWin() { return _win; }

    public boolean isAllLevelsComplete() { return _allLevelsComplete; }

    public int getMoveCount() { return _moveCount; }

    public int getMaxMoves() { return _maxMoves; }

    public Field getField() { return _field; }

    private void notifyListeners() {
        for (GameStateListener listener : _listeners) {
            listener.onGameStateChanged(this);
        }
    }
}

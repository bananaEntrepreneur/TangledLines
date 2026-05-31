package model.game.state;

import model.game.Field;
import model.listeners.GameStateListener;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    public enum Status {
        PLAYING,
        WON,
        LOST,
        ALL_LEVELS_COMPLETE
    }

    private Field _field;
    private int _maxMoves;
    private int _moveCount = 0;
    private Status _status = Status.PLAYING;
    private final List<GameStateListener> _listeners = new ArrayList<>();

    public GameState(Field field, int maxMoves) {
        _field = field;
        _maxMoves = maxMoves;
    }

    public void addListener(GameStateListener listener) {
        if (listener != null && !_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    public void loadLevel(Field field, int maxMoves) {
        _field = field;
        _maxMoves = maxMoves;
        _moveCount = 0;
        _status = Status.PLAYING;
    }

    public void incrementMoveCount() {
        _moveCount++;
        notifyListeners();
    }

    public void win() {
        _status = Status.WON;
        notifyListeners();
    }

    public void lose() {
        _status = Status.LOST;
        notifyListeners();
    }

    public void completeAllLevels() {
        _status = Status.ALL_LEVELS_COMPLETE;
        notifyListeners();
    }

    public boolean isGameOver() { return _status == Status.WON || _status == Status.LOST; }

    public boolean isWin() { return _status == Status.WON; }

    public boolean isAllLevelsComplete() { return _status == Status.ALL_LEVELS_COMPLETE; }

    public int getMoveCount() { return _moveCount; }

    public int getMaxMoves() { return _maxMoves; }

    public Field getField() { return _field; }

    private void notifyListeners() {
        for (GameStateListener listener : _listeners) {
            listener.onGameStateChanged(this);
        }
    }
}

package model.game;

import model.listeners.GameStateListener;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private enum Status {
        PLAYING,
        WON,
        LOST,
        ALL_LEVELS_COMPLETE
    }

    private Field _field;
    private int _maxMoveCount;
    private int _moveCount = 0;
    private Status _status = Status.PLAYING;
    private final List<GameStateListener> _listeners = new ArrayList<>();

    public GameState(Field field, int maxMoveCount) {
        setCurrentLevel(field, maxMoveCount);
    }

    public void addListener(GameStateListener listener) {
        if (listener != null && !_listeners.contains(listener)) {
            _listeners.add(listener);
            _listeners.sort((a, b) -> b.getPriority().getValue() - a.getPriority().getValue());
        }
    }

    public void removeListener(GameStateListener listener) { _listeners.remove(listener); }

    void startLevel(Field field, int maxMoveCount) {
        setCurrentLevel(field, maxMoveCount);
        notifyListeners();
    }

    void recordCommittedMove() {
        if (_status != Status.PLAYING) {
            return;
        }
        _moveCount++;
        notifyListeners();
    }

    void win() {
        if (_status != Status.PLAYING) {
            return;
        }
        _status = Status.WON;
        notifyListeners();
    }

    void lose() {
        if (_status != Status.PLAYING) {
            return;
        }
        _status = Status.LOST;
        notifyListeners();
    }

    void allLevelsComplete() {
        if (_status == Status.ALL_LEVELS_COMPLETE) {
            return;
        }
        _status = Status.ALL_LEVELS_COMPLETE;
        notifyListeners();
    }

    public boolean isCurrentLevelFinished() { return _status == Status.WON || _status == Status.LOST; }

    public boolean isCurrentLevelWon() { return _status == Status.WON; }

    public boolean isAllLevelsComplete() { return _status == Status.ALL_LEVELS_COMPLETE; }

    public int getMoveCount() { return _moveCount; }

    public int getMaxMoveCount() { return _maxMoveCount; }

    public Field getField() { return _field; }

    private void setCurrentLevel(Field field, int maxMoveCount) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        if (maxMoveCount < 1) {
            throw new IllegalArgumentException("maxMoveCount must be at least 1, got: " + maxMoveCount);
        }
        _field = field;
        _maxMoveCount = maxMoveCount;
        _moveCount = 0;
        _status = Status.PLAYING;
    }

    private void notifyListeners() {
        for (GameStateListener listener : _listeners) {
            listener.onGameStateChanged(this);
        }
    }
}

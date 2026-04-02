package model.game.state;

import model.game.Field;

public class GameState {
    private Field _field;
    private int _maxMoves;
    private int _moveCount = 0;
    private boolean _gameOver = false;
    private boolean _win = false;
    private boolean _allLevelsComplete = false;

    public GameState(Field field, int maxMoves) {
        _field = field;
        _maxMoves = maxMoves;
    }

    public void setField(Field field) { _field = field; }

    public void setMaxMoves(int maxMoves) { _maxMoves = maxMoves; }

    public void incrementMoveCount() { _moveCount++; }

    public void setGameOver(boolean gameOver) { _gameOver = gameOver; }

    public void setWin(boolean win) { _win = win; }

    public void setAllLevelsComplete(boolean allLevelsComplete) { _allLevelsComplete = allLevelsComplete; }

    public void reset() {
        _moveCount = 0;
        _gameOver = false;
        _win = false;
        _allLevelsComplete = false;
    }

    public boolean isGameOver() { return _gameOver; }

    public boolean isWin() { return _win; }

    public boolean isAllLevelsComplete() { return _allLevelsComplete; }

    public int getMoveCount() { return _moveCount; }

    public int getMaxMoves() { return _maxMoves; }

    public Field getField() { return _field; }
}

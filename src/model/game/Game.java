package model.game;

import model.game.intersection.IntersectionChecker;
import model.game.state.FieldProvider;
import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.LevelManager;
import model.listeners.NodeChangeListener;
import model.units.Node;

import java.awt.geom.Point2D;

public class Game implements NodeChangeListener, GameState, FieldProvider, LevelNavigation {
    private final LevelManager _levelManager;
    private final IntersectionChecker _intersectionChecker;
    private Field _field;
    private int _maxMoves;
    private int _moveCount = 0;
    private boolean _gameOver = false;
    private boolean _win = false;
    private boolean _started = false;
    private boolean _allLevelsComplete = false;

    public Game(LevelManager levelManager, IntersectionChecker intersectionChecker) {
        _levelManager = levelManager;
        _intersectionChecker = intersectionChecker;
        _field = levelManager.getCurrentField();
        _maxMoves = levelManager.getCurrentMaxMoves();
    }

    public void start() {
        if (_started) {
            return;
        }
        _started = true;
        subscribeToNodes();
    }

    public boolean moveNode(Node node, Point2D newPosition) {
        if (_gameOver || _allLevelsComplete) {
            return false;
        }
        return _field.moveNode(node, newPosition);
    }

    @Override
    public void onNodeMoved(Node node, Point2D newPosition) {
        if (_gameOver || _allLevelsComplete) {
            return;
        }

        _moveCount++;

        if (!_intersectionChecker.hasIntersections(_field.getEdges())) {
            _gameOver = true;
            _win = true;
        } else if (_moveCount >= _maxMoves) {
            _gameOver = true;
            _win = false;
        }
    }

    @Override
    public boolean nextLevel() {
        if (!_gameOver || !_win) {
            return false;
        }

        Field nextField = _levelManager.nextField();
        if (nextField == null) {
            _allLevelsComplete = true;
            return false;
        }

        resetForNewLevel(nextField);
        return true;
    }

    @Override
    public void restartLevel() { resetForNewLevel(_levelManager.getCurrentField()); }

    @Override
    public boolean isGameOver() { return _gameOver; }

    @Override
    public boolean isWin() { return _win; }

    @Override
    public boolean isAllLevelsComplete() { return _allLevelsComplete; }

    @Override
    public int getMoveCount() { return _moveCount; }

    @Override
    public int getMaxMoves() { return _maxMoves; }

    @Override
    public int getTotalLevels() { return _levelManager.getTotalLevels(); }

    @Override
    public Field getField() { return _field; }

    @Override
    public int getCurrentLevelIndex() { return _levelManager.getCurrentLevelIndex(); }

    @Override
    public boolean hasNextLevel() { return _levelManager.hasNextLevel(); }

    private void resetForNewLevel(Field newField) {
        _field = newField;
        _maxMoves = _levelManager.getCurrentMaxMoves();
        _moveCount = 0;
        _gameOver = false;
        _win = false;
        _started = false;
        start();
    }

    private void subscribeToNodes() {
        for (Node node : _field.getNodes()) {
            node.addListener(this);
        }
    }
}

package model.game;

import model.level.LevelManager;
import model.listeners.NodeChangeListener;
import model.units.Node;

import java.awt.geom.Point2D;

public class Game implements NodeChangeListener {
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
        _field = _levelManager.getCurrentField();
        _maxMoves = _levelManager.getCurrentMaxMoves();
    }

    public void start() {
        if (_started) {
            return;
        }
        _started = true;
        subscribeToNodes();
    }

    public boolean moveNode(Node node, Point2D newPosition) {
        if (_gameOver || _allLevelsComplete)
            return false;
        return _field.moveNode(node, newPosition);
    }

    @Override
    public void onNodeMoved(Node node, Point2D newPosition) {
        if (_gameOver || _allLevelsComplete)
            return;

        _moveCount++;

        if (_moveCount > _maxMoves) {
            _gameOver = true;
            _win = false;
        } else if (!_intersectionChecker.hasIntersections(_field.getEdges())) {
            _gameOver = true;
            _win = true;
        }
    }

    public boolean nextLevel() {
        if (!_gameOver || !_win) {
            return false;
        }
        Field nextField = _levelManager.nextField();
        if (nextField == null) {
            _allLevelsComplete = true;
            return false;
        }
        _field = nextField;
        _maxMoves = _levelManager.getCurrentMaxMoves();
        _moveCount = 0;
        _gameOver = false;
        _win = false;
        subscribeToNodes();
        return true;
    }

    public void restartLevel() {
        _field = _levelManager.getCurrentField();
        _maxMoves = _levelManager.getCurrentMaxMoves();
        _moveCount = 0;
        _gameOver = false;
        _win = false;
        subscribeToNodes();
    }

    public boolean isGameOver() { return _gameOver; }

    public boolean isWin() { return _win; }

    public boolean isAllLevelsComplete() { return _allLevelsComplete; }

    public Field getField() { return _field; }

    public int getMoveCount() { return _moveCount; }

    public int getMaxMoves() { return _maxMoves; }

    public int getCurrentLevelIndex() { return _levelManager.getCurrentLevelIndex(); }

    public int getTotalLevels() { return _levelManager.getTotalLevels(); }

    public boolean hasNextLevel() { return _levelManager.hasNextLevel(); }

    private void subscribeToNodes() {
        for (Node node : _field.getNodes()) {
            node.addListener(this);
        }
    }
}

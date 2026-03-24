package model.game;

import model.listeners.NodeChangeListener;
import model.units.Node;

import java.awt.geom.Point2D;

public class Game implements NodeChangeListener {
    private final Field _field;
    private final IntersectionChecker _intersectionChecker;
    private final int _maxMoves;
    private int _moveCount = 0;
    private boolean _gameOver = false;
    private boolean _win = false;

    public Game(Field field, int maxMoves, IntersectionChecker intersectionChecker) {
        _field = field;
        _maxMoves = maxMoves > 0 ? maxMoves : 1;
        _intersectionChecker = intersectionChecker;
        subscribeToNodes();
    }

    public boolean moveNode(Node node, Point2D newPosition) {
        if (_gameOver)
            return false;
        return _field.moveNode(node, newPosition);
    }

    @Override
    public void onNodeMoved(Node node, Point2D newPosition) {
        if (_gameOver)
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

    public boolean isGameOver() { return _gameOver; }

    public boolean isWin() { return _win; }

    public Field getField() { return _field; }

    public int getMoveCount() { return _moveCount; }

    public int getMaxMoves() { return _maxMoves; }

    private void subscribeToNodes() {
        for (Node node : _field.getNodes()) {
            node.addListener(this);
        }
    }
}

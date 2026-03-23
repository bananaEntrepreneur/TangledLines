package model.game;

import model.listeners.NodeChangeListener;
import model.units.Node;

import java.awt.geom.Point2D;

public class Game implements NodeChangeListener {
    private final Field _field;
    private final int _maxMoves;
    private int _moveCount = 0;
    private boolean _gameOver = false;
    private boolean _win = false;

    public Game(Field field, int maxMoves) {
        _field = field;
        _maxMoves = maxMoves;
    }

    public Field getField() {
        return _field;
    }

    public int get_moveCount() {
        return _moveCount;
    }

    public void start() { }

    public boolean moveNode(Node node, Point2D newPosition) {
        boolean moved = _field.moveNodeTo(node, newPosition);
        if (moved) {
            _moveCount++;
        }
        return moved;
    }

    public boolean hasWon() {
        return !_field.hasIntersections();
    }

    @Override
    public void onNodeMoved(Node node, Point2D oldPosition, Point2D newPosition) {
        if (_gameOver)
            return;

        _moveCount++;

        if (_moveCount > _maxMoves) {
            _gameOver = true;
            _win = false;
        }
    }
}

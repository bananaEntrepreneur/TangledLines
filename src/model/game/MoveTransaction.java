package model.game;

import model.listeners.TransactionListener;
import model.units.Node;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MoveTransaction {
    private enum State { IDLE, DRAGGING, COMMITTED, CANCELLED }

    private final Node _node;
    private final List<Point2D> _movementQueue = new ArrayList<>();
    private final TransactionListener _listener;
    private State _state = State.IDLE;

    public MoveTransaction(Node node, TransactionListener listener) {
        _node = node;
        _listener = listener;
    }

    public void startDragging() {
        if (_state != State.IDLE) return;
        _state = State.DRAGGING;
        _movementQueue.clear();
    }

    public void updateDragging(Point2D position) {
        if (_state != State.DRAGGING) return;
        _movementQueue.add(position);
    }

    public void stopDragging() {
        if (_state != State.DRAGGING) return;
        _state = State.COMMITTED;

        Point2D finalPosition = _movementQueue.isEmpty()
                ? _node.getPosition()
                : _movementQueue.getLast();

        if (_listener != null) {
            _listener.onCommitted(_node, finalPosition);
        }
    }

    public void cancelDragging() {
        if (_state != State.DRAGGING) return;
        _state = State.CANCELLED;
        _movementQueue.clear();
    }

    public Node getNode() { return _node; }
}
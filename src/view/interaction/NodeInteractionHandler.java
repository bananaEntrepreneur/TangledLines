package view.interaction;

import model.game.Game;
import model.units.Node;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class NodeInteractionHandler extends MouseAdapter {
    private final Game _game;
    private final NodeFinder _nodeFinder;
    private final DragListener _dragListener;
    private final HoverListener _hoverListener;

    private Node _hoveredNode = null;
    private Node _draggedNode = null;
    private Point _dragOffset = new Point();
    private Point2D _dragCurrentPosition = null;

    public NodeInteractionHandler(Game game, NodeFinder nodeFinder, 
                                   DragListener dragListener, HoverListener hoverListener) {
        _game = game;
        _nodeFinder = nodeFinder;
        _dragListener = dragListener;
        _hoverListener = hoverListener;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (_game.isGameOver()) return;

        _draggedNode = _nodeFinder.findNodeAt(e.getPoint());
        if (_draggedNode != null) {
            Point2D pos = _draggedNode.getPosition();
            _dragOffset = new Point(
                e.getX() - (int) pos.getX(),
                e.getY() - (int) pos.getY()
            );
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (_draggedNode != null && !_game.isGameOver()) {
            Point2D newPosition = new Point2D.Double(
                e.getX() - _dragOffset.x,
                e.getY() - _dragOffset.y
            );
            _game.moveNode(_draggedNode, newPosition);
        }
        _draggedNode = null;
        _dragCurrentPosition = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (_draggedNode != null && !_game.isGameOver()) {
            _dragCurrentPosition = new Point2D.Double(
                e.getX() - _dragOffset.x,
                e.getY() - _dragOffset.y
            );
            _dragListener.onDraggedNodeMoved(_draggedNode, _dragCurrentPosition);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Node node = _nodeFinder.findNodeAt(e.getPoint());
        if (node != _hoveredNode) {
            _hoveredNode = node;
            _hoverListener.onHoverChanged(node);
        }
    }

    public Node getDraggedNode() {
        return _draggedNode;
    }

    public Point2D getDragCurrentPosition() {
        return _dragCurrentPosition;
    }

    public Node getHoveredNode() {
        return _hoveredNode;
    }
}

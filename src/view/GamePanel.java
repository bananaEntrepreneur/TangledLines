package view;

import model.game.Game;
import model.units.Edge;
import model.units.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class GamePanel extends JPanel {
    private static final int NODE_RADIUS = 15;
    private static final Color EDGE_COLOR = new Color(70, 130, 180);
    private static final Color NODE_COLOR = new Color(220, 20, 60);
    private static final Color NODE_HOVER_COLOR = new Color(255, 105, 180);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);

    private final Game _game;
    private Node _hoveredNode = null;
    private Node _draggedNode = null;
    private Point _dragOffset = new Point();
    private Point2D _dragCurrentPosition = null;

    public GamePanel(Game game) {
        _game = game;
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(800, 600));

        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawEdges(g2d);
        drawNodes(g2d);
        drawStatus(g2d);

        g2d.dispose();
    }

    private void drawEdges(Graphics2D g2d) {
        g2d.setColor(EDGE_COLOR);
        g2d.setStroke(new BasicStroke(2));

        for (Edge edge : _game.getField().getEdges()) {
            Point2D a = edge.getNodeA().getPosition();
            Point2D b = edge.getNodeB().getPosition();

            if (edge.getNodeA() == _draggedNode && _dragCurrentPosition != null) {
                a = _dragCurrentPosition;
            }
            if (edge.getNodeB() == _draggedNode && _dragCurrentPosition != null) {
                b = _dragCurrentPosition;
            }
            
            g2d.drawLine(
                (int) a.getX(), (int) a.getY(),
                (int) b.getX(), (int) b.getY()
            );
        }
    }

    private void drawNodes(Graphics2D g2d) {
        for (Node node : _game.getField().getNodes()) {
            Point2D pos = node.getPosition();

            if (node == _draggedNode && _dragCurrentPosition != null) {
                pos = _dragCurrentPosition;
            }
            
            int x = (int) pos.getX() - NODE_RADIUS;
            int y = (int) pos.getY() - NODE_RADIUS;

            if (node == _hoveredNode || node == _draggedNode) {
                g2d.setColor(NODE_HOVER_COLOR);
            } else {
                g2d.setColor(NODE_COLOR);
            }

            g2d.fillOval(x, y, NODE_RADIUS * 2, NODE_RADIUS * 2);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, NODE_RADIUS * 2, NODE_RADIUS * 2);
        }
    }

    private void drawStatus(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        
        String status = "Moves: " + _game.getMoveCount() + "/" + 
            (_game.getField().getEdges().isEmpty() ? 0 : _game.getClass().getName().contains("Game") ? 
            getMoveLimit() : "∞");
        
        if (_game.isGameOver()) {
            status = _game.isWin() ? "YOU WIN!" : "GAME OVER";
            g2d.setColor(_game.isWin() ? Color.GREEN : Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
        }
        
        g2d.drawString(status, 20, 30);
    }

    private int getMoveLimit() {
        try {
            java.lang.reflect.Field field = Game.class.getDeclaredField("_maxMoves");
            field.setAccessible(true);
            return (int) field.get(_game);
        } catch (Exception e) {
            return -1;
        }
    }

    private Node findNodeAt(Point point) {
        for (Node node : _game.getField().getNodes()) {
            if (!node.isMovable()) continue;
            
            Point2D pos = node.getPosition();
            double dx = pos.getX() - point.x;
            double dy = pos.getY() - point.y;
            
            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) {
                return node;
            }
        }
        return null;
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (_game.isGameOver()) return;

            _draggedNode = findNodeAt(e.getPoint());
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
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Node node = findNodeAt(e.getPoint());
            if (node != _hoveredNode) {
                _hoveredNode = node;
                setCursor(node != null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) :
                    Cursor.getDefaultCursor());
                repaint();
            }
        }
    }
}

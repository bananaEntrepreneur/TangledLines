package view;

import model.game.Game;
import model.units.Edge;
import model.units.Node;
import view.interaction.DragListener;
import view.interaction.HoverListener;
import view.interaction.NodeFinder;
import view.interaction.NodeInteractionHandler;
import view.style.GameStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class GamePanel extends JPanel implements NodeFinder, DragListener, HoverListener {
    private final Game _game;
    private final NodeInteractionHandler _interactionHandler;

    public GamePanel(Game game) {
        _game = game;
        setBackground(GameStyle.BACKGROUND_COLOR);
        setPreferredSize(new Dimension(GameStyle.PANEL_WIDTH, GameStyle.PANEL_HEIGHT));

        _interactionHandler = new NodeInteractionHandler(game, this, this, this);
        addMouseListener(_interactionHandler);
        addMouseMotionListener(_interactionHandler);
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

    @Override
    public Node findNodeAt(Point point) {
        for (Node node : _game.getField().getNodes()) {
            if (!node.isMovable()) continue;

            Point2D pos = node.getPosition();
            double dx = pos.getX() - point.x;
            double dy = pos.getY() - point.y;

            if (dx * dx + dy * dy <= GameStyle.NODE_RADIUS * GameStyle.NODE_RADIUS) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void onDraggedNodeMoved(Node draggedNode, Point2D newPosition) {
        repaint();
    }

    @Override
    public void onHoverChanged(Node hoveredNode) {
        setCursor(hoveredNode != null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) :
            Cursor.getDefaultCursor());
        repaint();
    }

    private void drawEdges(Graphics2D g2d) {
        g2d.setColor(GameStyle.EDGE_COLOR);
        g2d.setStroke(new BasicStroke(2));

        for (Edge edge : _game.getField().getEdges()) {
            Point2D a = edge.getNodeA().getPosition();
            Point2D b = edge.getNodeB().getPosition();

            if (edge.getNodeA() == _interactionHandler.getDraggedNode()
                && _interactionHandler.getDragCurrentPosition() != null) {
                a = _interactionHandler.getDragCurrentPosition();
            }
            if (edge.getNodeB() == _interactionHandler.getDraggedNode()
                && _interactionHandler.getDragCurrentPosition() != null) {
                b = _interactionHandler.getDragCurrentPosition();
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

            if (node == _interactionHandler.getDraggedNode()
                && _interactionHandler.getDragCurrentPosition() != null) {
                pos = _interactionHandler.getDragCurrentPosition();
            }

            int x = (int) pos.getX() - GameStyle.NODE_RADIUS;
            int y = (int) pos.getY() - GameStyle.NODE_RADIUS;

            if (node == _interactionHandler.getHoveredNode() || node == _interactionHandler.getDraggedNode()) {
                g2d.setColor(GameStyle.NODE_HOVER_COLOR);
            } else if (!node.isMovable()) {
                g2d.setColor(GameStyle.UNMOVABLE_NODE_COLOR);
            } else {
                g2d.setColor(GameStyle.NODE_COLOR);
            }

            g2d.fillOval(x, y, GameStyle.NODE_RADIUS * 2, GameStyle.NODE_RADIUS * 2);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, GameStyle.NODE_RADIUS * 2, GameStyle.NODE_RADIUS * 2);
        }
    }

    private void drawStatus(Graphics2D g2d) {
        g2d.setColor(GameStyle.DEFAULT_STATUS_COLOR);
        g2d.setFont(new Font(GameStyle.STATUS_FONT_NAME, Font.BOLD, GameStyle.STATUS_FONT_SIZE));

        String status = "Moves: " + _game.getMoveCount() + "/" + _game.getMaxMoves();

        if (_game.isAllLevelsComplete()) {
            status = "ALL LEVELS COMPLETE!";
            g2d.setColor(GameStyle.ALL_COMPLETE_STATUS_COLOR);
            g2d.setFont(new Font(GameStyle.STATUS_FONT_NAME, Font.BOLD, GameStyle.GAME_OVER_FONT_SIZE));
        } else if (_game.isGameOver()) {
            status = _game.isWin() ? "LEVEL COMPLETE!" : "GAME OVER";
            g2d.setColor(_game.isWin() ? GameStyle.WIN_STATUS_COLOR : GameStyle.LOSE_STATUS_COLOR);
            g2d.setFont(new Font(GameStyle.STATUS_FONT_NAME, Font.BOLD, GameStyle.GAME_OVER_FONT_SIZE));
        }

        g2d.drawString(status, 20, 30);
    }
}

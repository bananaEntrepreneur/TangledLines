package view;

import model.game.GameState;
import model.units.Edge;
import model.units.Node;
import view.style.GameStyle;
import view.edge.EdgeRendererRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class EdgePanel extends JPanel {
    private final GameState _gameState;
    private final EdgeRendererRegistry _edgeRenderers;

    public EdgePanel(GameState gameState) {
        this(gameState, EdgeRendererRegistry.withDefaults());
    }

    public EdgePanel(GameState gameState, EdgeRendererRegistry edgeRenderers) {
        _gameState = gameState;
        if (edgeRenderers == null) {
            throw new IllegalArgumentException("Edge renderer registry cannot be null");
        }
        _edgeRenderers = edgeRenderers;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Edge edge : _gameState.getField().getEdges()) {
            drawEdge(g2d, edge);
        }

        drawStatus(g2d);
        g2d.dispose();
    }

    private void drawEdge(Graphics2D g2d, Edge edge) {
        Point2D a = drawPosition(edge.getNodeA());
        Point2D b = drawPosition(edge.getNodeB());
        _edgeRenderers.draw(g2d, edge, a, b);
    }

    private Point2D drawPosition(Node node) {
        return node.isDragging() ? node.getDragPosition() : node.getPosition();
    }

    private void drawStatus(Graphics2D g2d) {
        GameState state = _gameState;
        g2d.setColor(GameStyle.DEFAULT_STATUS_COLOR);
        g2d.setFont(new Font(GameStyle.STATUS_FONT_NAME, Font.BOLD, GameStyle.STATUS_FONT_SIZE));

        String status = GameStyle.LABEL_MOVES + state.getMoveCount() + "/" + state.getMaxMoveCount();

        if (state.isAllLevelsComplete()) {
            status = GameStyle.STATUS_ALL_COMPLETE;
            g2d.setColor(GameStyle.ALL_COMPLETE_STATUS_COLOR);
            g2d.setFont(new Font(GameStyle.STATUS_FONT_NAME, Font.BOLD, GameStyle.GAME_OVER_FONT_SIZE));
        } else if (state.isCurrentLevelFinished()) {
            status = state.isCurrentLevelWon() ? GameStyle.STATUS_LEVEL_COMPLETE : GameStyle.STATUS_GAME_OVER;
            g2d.setColor(state.isCurrentLevelWon() ? GameStyle.WIN_STATUS_COLOR : GameStyle.LOSE_STATUS_COLOR);
            g2d.setFont(new Font(GameStyle.STATUS_FONT_NAME, Font.BOLD, GameStyle.GAME_OVER_FONT_SIZE));
        }

        g2d.drawString(status, 20, 30);
    }
}

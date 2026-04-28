package view;

import model.game.Game;
import model.game.state.GameState;
import model.units.Edge;
import view.style.GameStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class EdgePanel extends JPanel {
    private final Game _game;

    public EdgePanel(Game game) {
        _game = game;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(GameStyle.EDGE_COLOR);
        g2d.setStroke(new BasicStroke(2));

        for (Edge edge : _game.getState().getField().getEdges()) {
            Point2D a = edge.getNodeA().isDragging() ? edge.getNodeA().getDragPosition() : edge.getNodeA().getPosition();
            Point2D b = edge.getNodeB().isDragging() ? edge.getNodeB().getDragPosition() : edge.getNodeB().getPosition();

            g2d.drawLine(
                (int) a.getX(), (int) a.getY(),
                (int) b.getX(), (int) b.getY()
            );
        }

        drawStatus(g2d);
        g2d.dispose();
    }

    private void drawStatus(Graphics2D g2d) {
        GameState state = _game.getState();
        g2d.setColor(GameStyle.DEFAULT_STATUS_COLOR);
        g2d.setFont(new Font(GameStyle.STATUS_FONT_NAME, Font.BOLD, GameStyle.STATUS_FONT_SIZE));

        String status = GameStyle.LABEL_MOVES + state.getMoveCount() + "/" + state.getMaxMoves();

        if (state.isAllLevelsComplete()) {
            status = GameStyle.STATUS_ALL_COMPLETE;
            g2d.setColor(GameStyle.ALL_COMPLETE_STATUS_COLOR);
            g2d.setFont(new Font(GameStyle.STATUS_FONT_NAME, Font.BOLD, GameStyle.GAME_OVER_FONT_SIZE));
        } else if (state.isGameOver()) {
            status = state.isWin() ? GameStyle.STATUS_LEVEL_COMPLETE : GameStyle.STATUS_GAME_OVER;
            g2d.setColor(state.isWin() ? GameStyle.WIN_STATUS_COLOR : GameStyle.LOSE_STATUS_COLOR);
            g2d.setFont(new Font(GameStyle.STATUS_FONT_NAME, Font.BOLD, GameStyle.GAME_OVER_FONT_SIZE));
        }

        g2d.drawString(status, 20, 30);
    }
}
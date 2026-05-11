package view;

import model.game.Game;
import model.game.state.GameState;
import model.units.Edge;
import model.units.BreakableEdge;
import model.units.Node;
import model.units.StretchableEdge;
import view.style.GameStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class EdgePanel extends JPanel {
    private final Game _game;
    private static final Stroke NORMAL_STROKE = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final Stroke WARNING_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final Stroke BROKEN_STROKE = new BasicStroke(
        3f,
        BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND,
        10f,
        new float[] { 12f, 10f },
        0f
    );

    public EdgePanel(Game game) {
        _game = game;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Edge edge : _game.getState().getField().getEdges()) {
            drawEdge(g2d, edge);
        }

        drawStatus(g2d);
        g2d.dispose();
    }

    private void drawEdge(Graphics2D g2d, Edge edge) {
        Point2D a = drawPosition(edge.getNodeA());
        Point2D b = drawPosition(edge.getNodeB());

        if (edge instanceof BreakableEdge breakableEdge) {
            if (breakableEdge.isBroken()) {
                g2d.setColor(GameStyle.BROKEN_EDGE_COLOR);
                g2d.setStroke(BROKEN_STROKE);
            } else if (breakableEdge.isReadyToBreak()) {
                g2d.setColor(GameStyle.EDGE_WARNING_COLOR);
                g2d.setStroke(WARNING_STROKE);
            } else {
                g2d.setColor(GameStyle.BREAKABLE_EDGE_COLOR);
                g2d.setStroke(NORMAL_STROKE);
            }
        } else if (edge instanceof StretchableEdge stretchableEdge) {
            if (stretchableEdge.isNearLimit()) {
                g2d.setColor(GameStyle.EDGE_WARNING_COLOR);
                g2d.setStroke(WARNING_STROKE);
            } else {
                g2d.setColor(GameStyle.STRETCHABLE_EDGE_COLOR);
                g2d.setStroke(NORMAL_STROKE);
            }
        } else {
            g2d.setColor(GameStyle.EDGE_COLOR);
            g2d.setStroke(NORMAL_STROKE);
        }

        g2d.drawLine(
            (int) a.getX(), (int) a.getY(),
            (int) b.getX(), (int) b.getY()
        );
    }

    private Point2D drawPosition(Node node) {
        return node.isDragging() ? node.getDragPosition() : node.getPosition();
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

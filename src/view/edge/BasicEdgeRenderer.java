package view.edge;

import model.units.Edge;
import view.style.GameStyle;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

public class BasicEdgeRenderer implements EdgeRenderer {
    protected static final Stroke NORMAL_STROKE = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    protected static final Stroke WARNING_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    @Override
    public boolean supports(Edge edge) {
        return edge != null;
    }

    @Override
    public void draw(Graphics2D g2d, Edge edge, Point2D start, Point2D end) {
        g2d.setColor(GameStyle.EDGE_COLOR);
        g2d.setStroke(NORMAL_STROKE);
        drawLine(g2d, start, end);
    }

    protected void drawLine(Graphics2D g2d, Point2D start, Point2D end) {
        g2d.drawLine(
            (int) start.getX(), (int) start.getY(),
            (int) end.getX(), (int) end.getY()
        );
    }
}

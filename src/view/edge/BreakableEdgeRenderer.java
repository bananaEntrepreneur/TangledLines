package view.edge;

import model.units.BreakableEdge;
import model.units.Edge;
import view.style.GameStyle;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

public class BreakableEdgeRenderer extends BasicEdgeRenderer {
    private static final Stroke BROKEN_STROKE = new BasicStroke(
        3f,
        BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND,
        10f,
        new float[] { 12f, 10f },
        0f
    );

    @Override
    public boolean supports(Edge edge) {
        return edge instanceof BreakableEdge;
    }

    @Override
    public void draw(Graphics2D g2d, Edge edge, Point2D start, Point2D end) {
        BreakableEdge breakableEdge = (BreakableEdge) edge;
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
        drawLine(g2d, start, end);
    }
}

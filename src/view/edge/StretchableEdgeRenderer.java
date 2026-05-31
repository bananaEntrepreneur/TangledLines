package view.edge;

import model.units.Edge;
import model.units.StretchableEdge;
import view.style.GameStyle;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class StretchableEdgeRenderer extends BasicEdgeRenderer {
    @Override
    public boolean supports(Edge edge) {
        return edge instanceof StretchableEdge;
    }

    @Override
    public void draw(Graphics2D g2d, Edge edge, Point2D start, Point2D end) {
        StretchableEdge stretchableEdge = (StretchableEdge) edge;
        if (stretchableEdge.isNearLimit()) {
            g2d.setColor(GameStyle.EDGE_WARNING_COLOR);
            g2d.setStroke(WARNING_STROKE);
        } else {
            g2d.setColor(GameStyle.STRETCHABLE_EDGE_COLOR);
            g2d.setStroke(NORMAL_STROKE);
        }
        drawLine(g2d, start, end);
    }
}

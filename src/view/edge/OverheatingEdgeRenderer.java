package view.edge;

import model.units.Edge;
import model.units.OverheatingEdge;
import view.style.GameStyle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

public class OverheatingEdgeRenderer extends BasicEdgeRenderer {
    private static final Stroke GLOW_STROKE = new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final Stroke HOT_STROKE = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    @Override
    public boolean supports(Edge edge) {
        return edge instanceof OverheatingEdge;
    }

    @Override
    public void draw(Graphics2D g2d, Edge edge, Point2D start, Point2D end) {
        OverheatingEdge overheatingEdge = (OverheatingEdge) edge;
        double heatRatio = overheatingEdge.getHeatRatio();
        Color color = colorFor(overheatingEdge);

        if (heatRatio > 0) {
            int alpha = (int) Math.min(180, 50 + heatRatio * 130);
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g2d.setStroke(GLOW_STROKE);
            drawLine(g2d, start, end);
        }

        g2d.setColor(color);
        g2d.setStroke(heatRatio >= 0.66 ? HOT_STROKE : NORMAL_STROKE);
        drawLine(g2d, start, end);
    }

    private Color colorFor(OverheatingEdge edge) {
        if (!edge.isActive()) {
            return GameStyle.BROKEN_EDGE_COLOR;
        }
        if (edge.getHeatRatio() >= 0.66) {
            return GameStyle.HOT_EDGE_COLOR;
        }
        if (edge.getHeatRatio() >= 0.33) {
            return GameStyle.WARM_EDGE_COLOR;
        }
        return GameStyle.OVERHEATING_EDGE_COLOR;
    }
}

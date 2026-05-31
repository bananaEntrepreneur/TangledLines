package view.edge;

import model.units.Edge;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public interface EdgeRenderer {
    boolean supports(Edge edge);

    void draw(Graphics2D g2d, Edge edge, Point2D start, Point2D end);
}

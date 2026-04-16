package model.listeners;

import java.awt.geom.Point2D;
import model.units.Node;

public interface NodeListener {
    void onMoved(Node node, Point2D newPosition);
}

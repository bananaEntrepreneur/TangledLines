package model.listeners;

import java.awt.geom.Point2D;
import model.units.Node;

public interface NodeChangeListener {
    void onNodeMoved(Node node, Point2D newPosition);
}

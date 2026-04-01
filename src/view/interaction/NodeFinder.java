package view.interaction;

import model.units.Node;
import java.awt.Point;

public interface NodeFinder {
    Node findNodeAt(Point point);
}

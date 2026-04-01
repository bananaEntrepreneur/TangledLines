package view.interaction;

import model.units.Node;

import java.awt.geom.Point2D;

public interface DragListener {
    void onDraggedNodeMoved(Node draggedNode, Point2D newPosition);
}

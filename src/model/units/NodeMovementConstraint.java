package model.units;

import java.awt.geom.Point2D;

public interface NodeMovementConstraint {
    Point2D constrain(Node node, Point2D desiredPosition);
}

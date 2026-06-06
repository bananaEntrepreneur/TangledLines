package model.units;

import java.awt.geom.Point2D;

interface NodeMovementConstraint {
    Point2D constrain(Node node, Point2D desiredPosition);
}

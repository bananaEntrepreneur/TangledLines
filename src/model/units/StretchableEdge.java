package model.units;

import java.awt.geom.Point2D;

public class StretchableEdge extends TensionedEdge implements NodeMovementConstraint {
    private final double _maxStretchPercent;

    public StretchableEdge(Node nodeA, Node nodeB, double stretchPercent) {
        super(nodeA, nodeB);
        if (stretchPercent < 0) {
            throw new IllegalArgumentException("Stretch percent cannot be negative");
        }
        _maxStretchPercent = stretchPercent;

        nodeA.addMovementConstraint(this);
        nodeB.addMovementConstraint(this);
    }

    public double getMaxLength() {
        return getOriginalLength() * (1.0 + _maxStretchPercent / 100.0);
    }

    public double getOriginalLength() {
        return super.getOriginalLength();
    }

    public double getCurrentLength() {
        return super.getCurrentLength();
    }

    public double getStretchFactor() {
        return getCurrentStretchFactor();
    }

    public boolean isNearLimit() {
        double maxLength = getMaxLength();
        if (maxLength <= 0) {
            return false;
        }
        return getCurrentLength() >= maxLength * 0.9;
    }

    @Override
    public Point2D constrain(Node node, Point2D desiredPosition) {
        Node other = getOtherNode(node);
        if (other == null || desiredPosition == null) {
            return desiredPosition;
        }

        double maxLength = getMaxLength();
        Point2D anchor = other.isDragging() ? other.getDragPosition() : other.getPosition();
        double desiredLength = anchor.distance(desiredPosition);

        if (getOriginalLength() <= 0 || desiredLength <= maxLength) {
            return desiredPosition;
        }

        if (desiredLength == 0) {
            return new Point2D.Double(anchor.getX(), anchor.getY());
        }

        double scale = maxLength / desiredLength;
        return new Point2D.Double(
            anchor.getX() + (desiredPosition.getX() - anchor.getX()) * scale,
            anchor.getY() + (desiredPosition.getY() - anchor.getY()) * scale
        );
    }
}

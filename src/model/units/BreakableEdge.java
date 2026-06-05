package model.units;

import model.listeners.NodeListener;

public class BreakableEdge extends TensionedEdge implements NodeListener {
    private final double _breakStretchPercent;
    private boolean _broken = false;

    public BreakableEdge(Node nodeA, Node nodeB, double breakStretchPercent) {
        super(nodeA, nodeB);
        if (breakStretchPercent < 0) {
            throw new IllegalArgumentException("Break percent cannot be negative");
        }
        _breakStretchPercent = breakStretchPercent;

        nodeA.addListener(this);
        nodeB.addListener(this);
    }

    public double getBreakLength() {
        return getOriginalLength() * (1.0 + _breakStretchPercent / 100.0);
    }

    @Override
    public boolean isActive() {
        return !_broken;
    }

    public boolean isReadyToBreak() {
        if (_broken) {
            return false;
        }

        double breakLength = getBreakLength();
        if (breakLength <= 0) {
            return getCurrentLength() > 0;
        }

        return getCurrentLength() >= breakLength * 0.9;
    }

    @Override
    public void onMoved(Node node) {
        if (!_broken && getCurrentStretchFactor() >= 1.0 + _breakStretchPercent / 100.0) {
            _broken = true;
        }
    }
}

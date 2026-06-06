package model.units;

import model.listeners.ListenerPriority;
import model.listeners.NodeListener;

public class BreakableEdge extends TensionedEdge {
    private final double _breakStretchPercent;
    private boolean _broken = false;
    private final NodeListener _listener = new NodeListener() {
        @Override
        public void onMoved(Node node) {
            updateBrokenState();
        }

        @Override
        public ListenerPriority getPriority() {
            return ListenerPriority.HIGH;
        }
    };

    public BreakableEdge(Node nodeA, Node nodeB, double breakStretchPercent) {
        super(nodeA, nodeB);
        if (breakStretchPercent < 0) {
            throw new IllegalArgumentException("Break percent cannot be negative");
        }
        _breakStretchPercent = breakStretchPercent;

        nodeA.addListener(_listener);
        nodeB.addListener(_listener);
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

    private void updateBrokenState() {
        if (!_broken && getCurrentStretchFactor() >= 1.0 + _breakStretchPercent / 100.0) {
            _broken = true;
        }
    }
}

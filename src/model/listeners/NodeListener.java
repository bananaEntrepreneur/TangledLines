package model.listeners;

import model.units.Node;

public interface NodeListener extends Listener {
    void onMoved(Node node);
}

package view;

import model.game.Game;
import model.listeners.NodeChangeListener;
import model.units.Node;

import java.awt.geom.Point2D;

public class View implements NodeChangeListener {
    private final GameFrame _frame;
    private final Game _game;

    public View(Game game) {
        _game = game;
        _frame = new GameFrame(game);
        subscribeToNodes();
    }

    private void subscribeToNodes() {
        for (Node node : _game.getField().getNodes()) {
            node.addListener(this);
        }
    }

    public void show() {
        _frame.setVisible(true);
    }

    @Override
    public void onNodeMoved(Node node, Point2D oldPosition, Point2D newPosition) {
        _frame.refresh();
    }
}

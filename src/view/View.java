package view;

import model.game.Game;
import model.game.state.FieldProvider;
import model.listeners.NodeChangeListener;
import model.units.Node;

import java.awt.geom.Point2D;

public class View implements NodeChangeListener {
    private final GameFrame _frame;
    private final FieldProvider _fieldProvider;

    public View(Game game) {
        _fieldProvider = game;
        _frame = new GameFrame(game, this);
        subscribeToNodes();
    }

    public void subscribeToNodes() {
        for (Node node : _fieldProvider.getField().getNodes()) {
            node.addListener(this);
        }
    }

    public void show() {
        _frame.setVisible(true);
    }

    @Override
    public void onNodeMoved(Node node, Point2D newPosition) {
        _frame.refresh();
    }
}

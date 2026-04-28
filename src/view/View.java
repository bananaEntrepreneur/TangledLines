package view;

import model.game.Game;
import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.listeners.GameStateListener;
import model.listeners.LevelNavigationListener;
import model.listeners.NodeListener;
import model.units.Node;

public class View implements NodeListener, LevelNavigationListener {
    private final GameFrame _frame;
    private final Game _game;

    public View(Game game) {
        _game = game;
        _frame = new GameFrame(game, this);
        _game.addLevelNavigationListener(this);
        refreshNodeSubscriptions();
    }

    public void refreshNodeSubscriptions() {
        for (Node node : _game.getState().getField().getNodes()) {
            node.removeListener(this);
            node.removeListener(_game);
            node.addListener(this);
            node.addListener(_game);
        }
    }

    public void show() {
        _frame.setVisible(true);
    }

    @Override
    public void onMoved(Node node) {
        _frame.refresh();
    }

    @Override
    public void onLevelChanged(LevelNavigation levelNavigation) {
        refreshNodeSubscriptions();
        _frame.refresh();
    }
}
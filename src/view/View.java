package view;

import model.game.Game;
import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.listeners.GameStateListener;
import model.listeners.LevelNavigationListener;
import model.listeners.NodeListener;
import model.units.Node;

import java.awt.geom.Point2D;

public class View implements NodeListener, GameStateListener, LevelNavigationListener {
    private final GameFrame _frame;
    private final Game _game;

    public View(Game game) {
        _game = game;
        _frame = new GameFrame(game, this);
    }

    public void subscribeToNodes() {
        for (Node node : _field.getNodes()) {
            node.addListener(this);
        }
    }

    public void show() {
        _frame.setVisible(true);
    }

    @Override
    public void onMoved(Node node, Point2D newPosition) {
        _frame.refresh();
    }

    @Override
    public void onGameStateChanged(GameState gameState) {
        _frame.refresh();
    }

    @Override
    public void onLevelChanged(LevelNavigation levelNavigation) {
        _frame.refresh();
    }
}

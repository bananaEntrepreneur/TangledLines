package model.game;

import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.LevelManager;
import model.listeners.LevelNavigationListener;
import model.listeners.NodeListener;
import model.units.Node;

public class Game implements NodeListener, LevelNavigationListener {
    private final GameState _state;
    private final LevelNavigation _navigation;
    private Field _attachedField;

    public Game(LevelManager levelManager) {
        _state = new GameState(levelManager.getCurrentField(), levelManager.getCurrentMaxMoves());
        _navigation = new LevelNavigation(levelManager, _state);
        _navigation.addListener(this);
        _attachedField = _state.getField();
        attachToNodesFromCurrentField();
    }

    public GameState getState() { return _state; }
    public LevelNavigation getNavigation() { return _navigation; }

    @Override
    public void onMoved(Node node) {
        if (_state.isGameOver() || _state.isAllLevelsComplete()) return;

        _state.incrementMoveCount();

        if (_state.getField().hasFailedEdges()) {
            _state.lose();
        } else if (!_state.getField().hasIntersections()) {
            _state.win();
        } else if (_state.getMoveCount() >= _state.getMaxMoves()) {
            _state.lose();
        }
    }

    @Override
    public void onLevelChanged(LevelNavigation levelNavigation) {
        if (_state.getField() != _attachedField) {
            attachToNodesFromCurrentField();
        }
    }

    private void attachToNodesFromCurrentField() {
        _attachedField = _state.getField();
        _state.getField().getNodes().forEach(node -> node.addListener(this));
    }
}

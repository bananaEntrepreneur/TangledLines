package model.game;

import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.LevelManager;
import model.listeners.LevelNavigationListener;
import model.listeners.ListenerPriority;
import model.listeners.NodeListener;
import model.units.Node;

public class Game {
    private final GameState _state;
    private final LevelNavigation _navigation;
    private Field _observedField;
    private final NodeListener _nodeListener = new NodeListener() {
        @Override
        public void onMoved(Node node) {
            checkAfterMove(node);
        }

        @Override
        public ListenerPriority getPriority() {
            return ListenerPriority.HIGH;
        }
    };
    private final LevelNavigationListener _levelNavigationListener = new LevelNavigationListener() {
        @Override
        public void onLevelChanged(LevelNavigation levelNavigation) {
            attachToNodesFromCurrentField();
        }

        @Override
        public ListenerPriority getPriority() {
            return ListenerPriority.MEDIUM;
        }
    };

    public Game(LevelManager levelManager) {
        _state = new GameState(levelManager.getCurrentField(), levelManager.getCurrentMaxMoves());
        _navigation = new LevelNavigation(levelManager, _state);
        _navigation.addListener(_levelNavigationListener);
        attachToNodesFromCurrentField();
    }

    public GameState getState() { return _state; }
    public LevelNavigation getNavigation() { return _navigation; }

    private void checkAfterMove(Node node) {
        if (_state.isGameOver() || _state.isAllLevelsComplete()) return;

        boolean hasIntersections = _observedField.hasIntersections();

        if (_observedField.hasInactiveEdges()) {
            _state.lose();
            return;
        }

        if (node.isDragging()) {
            return;
        }

        _state.incrementMoveCount();

        if (!hasIntersections) {
            _state.win();
        } else if (_state.getMoveCount() >= _state.getMaxMoves()) {
            _state.lose();
        }
    }

    private void attachToNodesFromCurrentField() {
        if (_observedField != null) {
            _observedField.getNodes().forEach(node -> node.removeListener(_nodeListener));
        }

        _observedField = _state.getField();
        _observedField.getNodes().forEach(node -> node.addListener(_nodeListener));
    }
}

package model.game;

import model.level.LevelManager;
import model.listeners.LevelNavigationListener;
import model.listeners.Priority;
import model.listeners.NodeListener;
import model.units.Node;

public class Game {
    private final GameState _state;
    private final LevelNavigation _navigation;
    private Field _observedField;
    private final NodeListener _nodeListener = new NodeListener() {
        @Override
        public void onMoved(Node node) {
            updateStateAfterNodeMovement(node);
        }

        @Override
        public Priority getPriority() {
            return Priority.HIGH;
        }
    };
    private final LevelNavigationListener _levelNavigationListener = new LevelNavigationListener() {
        @Override
        public void onLevelChanged(LevelNavigation levelNavigation) {
            attachToNodesFromCurrentField();
        }

        @Override
        public Priority getPriority() {
            return Priority.MEDIUM;
        }
    };

    public Game(LevelManager levelManager) {
        _state = new GameState(levelManager.createCurrentField(), levelManager.getCurrentMaxMoveCount());
        _navigation = new LevelNavigation(levelManager, _state);
        _navigation.addListener(_levelNavigationListener);
        attachToNodesFromCurrentField();
    }

    public GameState getState() { return _state; }
    public LevelNavigation getLevelNavigation() { return _navigation; }

    private void updateStateAfterNodeMovement(Node node) {
        if (_state.isCurrentLevelFinished() || _state.isAllLevelsComplete()) return;

        boolean hasIntersections = _observedField.hasIntersections();

        if (_observedField.hasInactiveEdges()) {
            _state.lose();
            return;
        }

        if (node.isDragging()) {
            return;
        }

        _state.recordCommittedMove();

        if (!hasIntersections) {
            _state.win();
        } else if (_state.getMoveCount() >= _state.getMaxMoveCount()) {
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

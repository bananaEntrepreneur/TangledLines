package view;

import model.game.GameState;
import model.listeners.Priority;
import model.listeners.NodeListener;
import model.units.Node;
import view.style.GameStyle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private final GameState _gameState;
    private final List<NodeWidget> _nodeWidgets = new ArrayList<>();
    private final EdgePanel _edgePanel;
    private final NodeListener _nodeListener = new NodeListener() {
        @Override
        public void onMoved(Node node) {
            updateWidgetPosition(node);
            _edgePanel.repaint();
        }

        @Override
        public Priority getPriority() {
            return Priority.MEDIUM;
        }
    };

    public GamePanel(GameState gameState) {
        _gameState = gameState;
        setLayout(null);
        setBackground(GameStyle.BACKGROUND_COLOR);
        setPreferredSize(new Dimension(GameStyle.PANEL_WIDTH, GameStyle.PANEL_HEIGHT));

        _edgePanel = new EdgePanel(gameState);
        _edgePanel.setBounds(0, 0, GameStyle.PANEL_WIDTH, GameStyle.PANEL_HEIGHT);
        add(_edgePanel);

        createNodeWidgets();
    }

    public void recreateWidgets() {
        unsubscribeFromNodes();
        for (NodeWidget widget : _nodeWidgets) {
            remove(widget);
        }
        _nodeWidgets.clear();
        createNodeWidgets();
        subscribeToNodes();
        revalidate();
        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        subscribeToNodes();
    }

    @Override
    public void removeNotify() {
        unsubscribeFromNodes();
        super.removeNotify();
    }

    private void subscribeToNodes() {
        for (NodeWidget widget : _nodeWidgets) {
            widget.getNode().addListener(_nodeListener);
        }
    }

    private void unsubscribeFromNodes() {
        for (NodeWidget widget : _nodeWidgets) {
            widget.getNode().removeListener(_nodeListener);
        }
    }

    private void createNodeWidgets() {
        for (Node node : _gameState.getField().getNodes()) {
            addNodeWidget(node);
        }
    }

    private void addNodeWidget(Node node) {
        NodeWidget widget = new NodeWidget(node);
        widget.syncWithNode();
        add(widget, 0);
        _nodeWidgets.add(widget);
    }

    private void updateWidgetPosition(Node node) {
        for (NodeWidget widget : _nodeWidgets) {
            if (widget.getNode() == node) {
                widget.syncWithNode();
            }
        }
    }
}

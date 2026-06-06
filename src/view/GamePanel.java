package view;

import model.game.GameState;
import model.listeners.Priority;
import model.listeners.NodeListener;
import model.units.Node;
import view.style.GameStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
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
        for (NodeWidget widget : _nodeWidgets) {
            widget.getNode().removeListener(_nodeListener);
            remove(widget);
        }
        _nodeWidgets.clear();
        createNodeWidgets();
        reorderNodeWidgets();
        revalidate();
        repaint();
    }

    private void reorderNodeWidgets() {
        for (Component c : getComponents()) {
            if (c instanceof NodeWidget) {
                setComponentZOrder(c, 0);
            }
        }
    }

    private void createNodeWidgets() {
        for (Node node : _gameState.getField().getNodes()) {
            addNodeWidget(node);
        }
        reorderNodeWidgets();
    }

    private void addNodeWidget(Node node) {
        NodeWidget widget = new NodeWidget(node);
        Point2D pos = node.getPosition();
        widget.setBounds(
            (int) pos.getX() - GameStyle.NODE_RADIUS,
            (int) pos.getY() - GameStyle.NODE_RADIUS,
            GameStyle.NODE_RADIUS * 2,
            GameStyle.NODE_RADIUS * 2
        );
        add(widget);
        _nodeWidgets.add(widget);
        node.addListener(_nodeListener);
    }

    private void updateWidgetPosition(Node node) {
        for (NodeWidget widget : _nodeWidgets) {
            if (widget.getNode() == node) {
                Point2D pos = node.isDragging() ? node.getDragPosition() : node.getPosition();
                widget.setBounds(
                    (int) pos.getX() - GameStyle.NODE_RADIUS,
                    (int) pos.getY() - GameStyle.NODE_RADIUS,
                    GameStyle.NODE_RADIUS * 2,
                    GameStyle.NODE_RADIUS * 2
                );
            }
        }
    }
}

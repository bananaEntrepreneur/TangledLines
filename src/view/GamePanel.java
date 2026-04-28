package view;

import model.game.Game;
import model.listeners.NodeListener;
import model.units.Node;
import view.style.GameStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements NodeListener {
    private final Game _game;
    private final List<NodeWidget> _nodeWidgets = new ArrayList<>();
    private final EdgePanel _edgePanel;

    public GamePanel(Game game) {
        _game = game;
        setLayout(null);
        setBackground(GameStyle.BACKGROUND_COLOR);
        setPreferredSize(new Dimension(GameStyle.PANEL_WIDTH, GameStyle.PANEL_HEIGHT));

        _edgePanel = new EdgePanel(game);
        _edgePanel.setBounds(0, 0, GameStyle.PANEL_WIDTH, GameStyle.PANEL_HEIGHT);
        add(_edgePanel);

        createNodeWidgets();
    }

    public void recreateWidgets() {
        for (NodeWidget widget : _nodeWidgets) {
            widget.getNode().removeListener(this);
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
        for (Node node : _game.getState().getField().getNodes()) {
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
        node.addListener(this);
    }

    @Override
    public void onMoved(Node node) {
        updateWidgetPosition(node);
        _edgePanel.repaint();
    }

    private void updateWidgetPosition(Node node) {
        for (NodeWidget widget : _nodeWidgets) {
            if (widget.getNode() == node) {
                Point2D pos = node.getPosition();
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
package view;

import model.listeners.NodeListener;
import model.units.Node;
import view.style.GameStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class NodeWidget extends JComponent implements NodeListener {
    private final Node _node;
    private boolean _isDragging = false;
    private Point _dragOffset = new Point();

    public NodeWidget(Node node) {
        _node = node;
        _node.addListener(this);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(true);
        setVisible(true);
        setupMouseHandlers();
    }

    private void setupMouseHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                _isDragging = true;
                Point mousePos = SwingUtilities.convertPoint(NodeWidget.this, e.getPoint(), getParent());
                Point2D pos = _node.getPosition();
                _dragOffset = new Point(
                    mousePos.x - (int) pos.getX(),
                    mousePos.y - (int) pos.getY()
                );
                _node.startDragging();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (_isDragging) {
                    _isDragging = false;
                    _node.stopDragging();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (_isDragging) {
                    Point mousePos = SwingUtilities.convertPoint(NodeWidget.this, e.getPoint(), getParent());
                    Point2D newPos = new Point2D.Double(
                        mousePos.x - _dragOffset.x,
                        mousePos.y - _dragOffset.y
                    );
                    _node.updateDragging(newPos);
                    setLocation(
                        (int) newPos.getX() - GameStyle.NODE_RADIUS,
                        (int) newPos.getY() - GameStyle.NODE_RADIUS
                    );
                    repaint();
                    if (getParent() != null) {
                        getParent().repaint();
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = GameStyle.NODE_RADIUS * 2;
        Color baseColor = _isDragging ? GameStyle.NODE_HOVER_COLOR : GameStyle.NODE_COLOR;

        g2d.setColor(baseColor);
        g2d.fillOval(1, 1, size - 2, size - 2);

        g2d.setColor(baseColor.darker());
        g2d.drawOval(0, 0, size - 1, size - 1);

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(2, 2, size / 3, size / 3);

        g2d.dispose();
    }

    @Override
    public void onMoved(Node node) {
        repaint();
    }

    public Node getNode() {
        return _node;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GameStyle.NODE_RADIUS * 2, GameStyle.NODE_RADIUS * 2);
    }
}
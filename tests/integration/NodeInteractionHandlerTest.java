package integration;

import model.game.Game;
import model.level.LevelLoadException;
import model.level.LevelManager;
import model.units.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import view.interaction.DragListener;
import view.interaction.HoverListener;
import view.interaction.NodeInteractionHandler;

import javax.swing.JPanel;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NodeInteractionHandler Integration Tests")
class NodeInteractionHandlerTest {

    private LevelManager _levelManager;
    private Game _game;
    private Node _movableNode;
    private NodeInteractionHandler _handler;
    private RecordingDragListener _dragListener;
    private RecordingHoverListener _hoverListener;
    private NodeFinder _nodeFinder;
    private JPanel _component;

    @BeforeEach
    void setUp() throws LevelLoadException {
        _levelManager = new LevelManager("levels");
        _game = new Game(_levelManager);
        _movableNode = _game.getField().getNodes().stream()
                .filter(Node::isMovable)
                .findFirst()
                .orElseThrow();
        attachGameToField();

        _dragListener = new RecordingDragListener();
        _hoverListener = new RecordingHoverListener();
        _nodeFinder = point -> _movableNode;
        _component = new JPanel();
        _handler = new NodeInteractionHandler(_game, _nodeFinder, _dragListener, _hoverListener);
    }

    @Nested
    @DisplayName("Mouse Drag Flow")
    class MouseDragFlowTests {

        @Test
        @DisplayName("Should queue drag updates and commit on release")
        void shouldCommitDraggedPosition() {
            Point start = new Point((int) _movableNode.getPosition().getX(), (int) _movableNode.getPosition().getY());

            _handler.mousePressed(mouseEvent(MouseEvent.MOUSE_PRESSED, start.x, start.y));
            _handler.mouseDragged(mouseEvent(MouseEvent.MOUSE_DRAGGED, start.x + 60, start.y + 40));
            _handler.mouseReleased(mouseEvent(MouseEvent.MOUSE_RELEASED, start.x + 60, start.y + 40));

            assertEquals(1, _dragListener.calls.get());
            assertEquals(new Point2D.Double(start.x + 60, start.y + 40), _dragListener.lastPosition.get());
            assertEquals(new Point2D.Double(start.x + 60, start.y + 40), _movableNode.getPosition());
            assertNull(_handler.getDraggedNode());
            assertNull(_handler.getDragCurrentPosition());
        }

        @Test
        @DisplayName("Should ignore drag when game is over")
        void shouldIgnoreDragWhenGameOver() {
            _game.restartLevel();
            forceGameOver();

            Point start = new Point((int) _movableNode.getPosition().getX(), (int) _movableNode.getPosition().getY());
            _handler.mousePressed(mouseEvent(MouseEvent.MOUSE_PRESSED, start.x, start.y));
            _handler.mouseDragged(mouseEvent(MouseEvent.MOUSE_DRAGGED, start.x + 20, start.y + 20));

            assertNull(_handler.getDraggedNode());
            assertNull(_handler.getDragCurrentPosition());
            assertEquals(0, _dragListener.calls.get());
        }
    }

    @Nested
    @DisplayName("Hover Flow")
    class HoverFlowTests {

        @Test
        @DisplayName("Should update hovered node when pointer moves over a node")
        void shouldUpdateHoveredNode() {
            _handler.mouseMoved(mouseEvent(MouseEvent.MOUSE_MOVED, 1, 1));

            assertSame(_movableNode, _handler.getHoveredNode());
            assertEquals(1, _hoverListener.calls.get());
            assertSame(_movableNode, _hoverListener.lastNode.get());
        }

        @Test
        @DisplayName("Should keep hover listener stable for repeated moves over same node")
        void shouldNotRetokenizeSameHover() {
            _handler.mouseMoved(mouseEvent(MouseEvent.MOUSE_MOVED, 1, 1));
            _handler.mouseMoved(mouseEvent(MouseEvent.MOUSE_MOVED, 2, 2));

            assertEquals(1, _hoverListener.calls.get());
        }
    }

    private MouseEvent mouseEvent(int id, int x, int y) {
        return new MouseEvent(_component, id, System.currentTimeMillis(), 0, x, y, 1, false);
    }

    private void attachGameToField() {
        for (Node node : _game.getField().getNodes()) {
            node.removeListener(_game);
            node.addListener(_game);
        }
    }

    private void forceGameOver() {
        for (int i = 0; i <= _game.getMaxMoves(); i++) {
            _movableNode.move(new Point2D.Double(
                    _movableNode.getPosition().getX() + 100,
                    _movableNode.getPosition().getY()
            ));
        }
    }

    private static class RecordingDragListener implements DragListener {
        final AtomicInteger calls = new AtomicInteger(0);
        final AtomicReference<Node> lastNode = new AtomicReference<>();
        final AtomicReference<Point2D> lastPosition = new AtomicReference<>();

        @Override
        public void onDraggedNodeMoved(Node draggedNode, Point2D newPosition) {
            calls.incrementAndGet();
            lastNode.set(draggedNode);
            lastPosition.set(newPosition);
        }
    }

    private static class RecordingHoverListener implements HoverListener {
        final AtomicInteger calls = new AtomicInteger(0);
        final AtomicReference<Node> lastNode = new AtomicReference<>();

        @Override
        public void onHoverChanged(Node hoveredNode) {
            calls.incrementAndGet();
            lastNode.set(hoveredNode);
        }
    }
}

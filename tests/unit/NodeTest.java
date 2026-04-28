package unit;

import model.listeners.NodeListener;
import model.units.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Node Unit Tests")
class NodeTest {

    private Point2D _initialPosition;
    private Point2D _newPosition;

    @BeforeEach
    void setUp() {
        _initialPosition = new Point2D.Double(100, 100);
        _newPosition = new Point2D.Double(200, 200);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create node with given position")
        void shouldCreateNode() {
            Point2D position = new Point2D.Double(50, 50);
            Node node = new Node(position);

            assertEquals(position, node.getPosition());
        }
    }

    @Nested
    @DisplayName("Dragging Tests")
    class DraggingTests {

        @Test
        @DisplayName("Should start in not dragging state")
        void shouldStartNotDragging() {
            Node node = new Node(_initialPosition);

            assertFalse(node.isDragging());
        }

        @Test
        @DisplayName("Should update position during dragging")
        void shouldUpdatePositionDuringDragging() {
            Node node = new Node(_initialPosition);

            node.startDragging();
            node.updateDragging(_newPosition);
            node.stopDragging();

            assertEquals(_newPosition, node.getPosition());
        }

        @Test
        @DisplayName("Should notify listeners when drag completes")
        void shouldNotifyListenersOnDragComplete() {
            Node node = new Node(_initialPosition);
            MockNodeListener listener = new MockNodeListener();
            node.addListener(listener);

            node.startDragging();
            node.updateDragging(_newPosition);
            node.stopDragging();

            assertTrue(listener.wasNotified());
        }

        @Test
        @DisplayName("Should not update position without startDragging")
        void shouldNotUpdateWithoutStartDragging() {
            Node node = new Node(_initialPosition);

            node.updateDragging(_newPosition);
            node.stopDragging();

            assertEquals(_initialPosition, node.getPosition());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class SetPositionEdgeCases {

        @Test
        @DisplayName("Should not update position when null position during drag")
        void shouldNotUpdatePositionWhenNull() {
            Node node = new Node(_initialPosition);

            node.startDragging();
            node.updateDragging(null);
            node.stopDragging();

            assertEquals(_initialPosition, node.getPosition());
        }

        @Test
        @DisplayName("Should not notify listeners when null position")
        void shouldNotNotifyListenersWhenNull() {
            Node node = new Node(_initialPosition);
            MockNodeListener listener = new MockNodeListener();
            node.addListener(listener);

            node.startDragging();
            node.updateDragging(null);
            node.stopDragging();

            assertFalse(listener.wasNotified());
        }

        @Test
        @DisplayName("Should not update position when same position")
        void shouldNotUpdatePositionWhenSame() {
            Node node = new Node(_initialPosition);

            node.startDragging();
            node.updateDragging(_initialPosition);
            node.stopDragging();

            assertEquals(_initialPosition, node.getPosition());
        }

        @Test
        @DisplayName("Should not notify listeners when same position")
        void shouldNotNotifyListenersWhenSame() {
            Node node = new Node(_initialPosition);
            MockNodeListener listener = new MockNodeListener();
            node.addListener(listener);

            node.startDragging();
            node.updateDragging(_initialPosition);
            node.stopDragging();

            assertFalse(listener.wasNotified());
        }

        @Test
        @DisplayName("Should not commit without queued position")
        void shouldNotCommitWithoutQueuedPosition() {
            Node node = new Node(_initialPosition);

            node.startDragging();
            node.stopDragging();

            assertEquals(_initialPosition, node.getPosition());
        }
    }

    @Nested
    @DisplayName("Listener Tests")
    class ListenerTests {

        @Test
        @DisplayName("Should support multiple listeners")
        void shouldSupportMultipleListeners() {
            Node node = new Node(_initialPosition);
            MockNodeListener listener1 = new MockNodeListener();
            MockNodeListener listener2 = new MockNodeListener();
            node.addListener(listener1);
            node.addListener(listener2);

            node.startDragging();
            node.updateDragging(_newPosition);
            node.stopDragging();

            assertTrue(listener1.wasNotified());
            assertTrue(listener2.wasNotified());
        }

        @Test
        @DisplayName("Should notify all listeners with same position")
        void shouldNotifyAllListenersWithSamePosition() {
            Node node = new Node(_initialPosition);
            MockNodeListener listener1 = new MockNodeListener();
            MockNodeListener listener2 = new MockNodeListener();
            node.addListener(listener1);
            node.addListener(listener2);

            node.startDragging();
            node.updateDragging(_newPosition);
            node.stopDragging();

            assertTrue(listener1.wasNotified());
            assertTrue(listener2.wasNotified());
        }

        @Test
        @DisplayName("Should notify listeners added after creation")
        void shouldNotifyListenersAddedAfterCreation() {
            Node node = new Node(_initialPosition);

            MockNodeListener listener = new MockNodeListener();
            node.addListener(listener);

            node.startDragging();
            node.updateDragging(_newPosition);
            node.stopDragging();

            assertTrue(listener.wasNotified());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should return correct position")
        void shouldReturnCorrectPosition() {
            Point2D position = new Point2D.Double(300, 400);
            Node node = new Node(position);

            assertEquals(position, node.getPosition());
        }

        @Test
        @DisplayName("Should return drag position during dragging")
        void shouldReturnDragPosition() {
            Node node = new Node(_initialPosition);

            node.startDragging();
            node.updateDragging(_newPosition);

            assertEquals(_newPosition, node.getDragPosition());
        }

        @Test
        @DisplayName("Should return current position when not dragging")
        void shouldReturnCurrentPositionWhenNotDragging() {
            Node node = new Node(_initialPosition);

            assertEquals(_initialPosition, node.getDragPosition());
        }
    }

    private static class MockNodeListener implements NodeListener {
        private boolean _notified = false;

        @Override
        public void onMoved(Node node) {
            _notified = true;
        }

        public boolean wasNotified() {
            return _notified;
        }

    }
}
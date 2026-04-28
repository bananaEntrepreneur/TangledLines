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
        @DisplayName("Should create movable node with given position")
        void shouldCreateMovableNode() {
            Point2D position = new Point2D.Double(50, 50);
            Node node = new Node(position);

            assertEquals(position, node.getPosition());
            assertTrue(node.isMovable());
        }

        @Test
        @DisplayName("Should create non-movable node with given position")
        void shouldCreateNonMovableNode() {
            Point2D position = new Point2D.Double(150, 150);
            Node node = new Node(position);

            assertEquals(position, node.getPosition());
            assertFalse(node.isMovable());
        }
    }

    @Nested
    @DisplayName("setPosition Tests - Movable Node")
    class SetPositionMovableTests {

        @Test
        @DisplayName("Should update position for movable node")
        void shouldUpdatePositionForMovableNode() {
            Node node = new Node(_initialPosition);

            node.move(_newPosition);

            assertEquals(_newPosition, node.getPosition());
        }

        @Test
        @DisplayName("Should notify listeners when position changes")
        void shouldNotifyListenersOnPositionChange() {
            Node node = new Node(_initialPosition);
            MockNodeListener listener = new MockNodeListener();
            node.addListener(listener);

            node.move(_newPosition);

            assertTrue(listener.wasNotified());
        }
    }

    @Nested
    @DisplayName("setPosition Tests - Non-Movable Node")
    class SetPositionNonMovableTests {

        @Test
        @DisplayName("Should not update position for non-movable node")
        void shouldNotUpdatePositionForNonMovableNode() {
            Node node = new Node(_initialPosition);

            node.move(_newPosition);

            assertEquals(_initialPosition, node.getPosition());
        }

        @Test
        @DisplayName("Should not notify listeners for non-movable node")
        void shouldNotNotifyListenersForNonMovableNode() {
            Node node = new Node(_initialPosition);
            MockNodeListener listener = new MockNodeListener();
            node.addListener(listener);

            node.move(_newPosition);

            assertFalse(listener.wasNotified());
        }
    }

    @Nested
    @DisplayName("setPosition Tests - Edge Cases")
    class SetPositionEdgeCases {

        @Test
        @DisplayName("Should not update position when newPosition is null")
        void shouldNotUpdatePositionWhenNull() {
            Node node = new Node(_initialPosition);

            node.move(null);

            assertEquals(_initialPosition, node.getPosition());
        }

        @Test
        @DisplayName("Should not notify listeners when newPosition is null")
        void shouldNotNotifyListenersWhenNull() {
            Node node = new Node(_initialPosition);
            MockNodeListener listener = new MockNodeListener();
            node.addListener(listener);

            node.move(null);

            assertFalse(listener.wasNotified());
        }

        @Test
        @DisplayName("Should not update position when newPosition equals current position")
        void shouldNotUpdatePositionWhenSame() {
            Node node = new Node(_initialPosition);

            node.move(_initialPosition);

            assertEquals(_initialPosition, node.getPosition());
        }

        @Test
        @DisplayName("Should not notify listeners when newPosition equals current position")
        void shouldNotNotifyListenersWhenSame() {
            Node node = new Node(_initialPosition);
            MockNodeListener listener = new MockNodeListener();
            node.addListener(listener);

            node.move(_initialPosition);

            assertFalse(listener.wasNotified());
        }

        @Test
        @DisplayName("Should not update position when newPosition equals current position (different object)")
        void shouldNotUpdatePositionWhenEqual() {
            Node node = new Node(_initialPosition);
            Point2D equalPosition = new Point2D.Double(100, 100);

            node.move(equalPosition);

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

            node.move(_newPosition);

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

            node.move(_newPosition);

            assertTrue(listener1.wasNotified());
            assertTrue(listener2.wasNotified());
        }

        @Test
        @DisplayName("Should notify listeners added after creation")
        void shouldNotifyListenersAddedAfterCreation() {
            Node node = new Node(_initialPosition);

            MockNodeListener listener = new MockNodeListener();
            node.addListener(listener);

            node.move(_newPosition);

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
        @DisplayName("Should return correct movability status")
        void shouldReturnCorrectMovability() {
            Node movableNode = new Node(_initialPosition);
            Node nonMovableNode = new Node(_initialPosition);

            assertTrue(movableNode.isMovable());
            assertFalse(nonMovableNode.isMovable());
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

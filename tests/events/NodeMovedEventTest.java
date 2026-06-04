package events;

import model.listeners.NodeListener;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("NodeListener.onMoved Event Tests")
class NodeMovedEventTest {

    @Test
    @DisplayName("Should call node listeners in order after drag state changes")
    void shouldCallNodeListenersInOrderAfterDragStateChanges() {
        Node node = new Node(new Point2D.Double(0, 0));
        List<String> events = new ArrayList<>();

        node.addListener(movedNode -> events.add("first:" + describe(movedNode)));
        node.addListener(movedNode -> events.add("second:" + describe(movedNode)));

        node.startDragging();
        node.updateDragging(new Point2D.Double(10, 0));
        node.stopDragging();

        assertEquals(List.of(
            "first:dragging position=0 drag=10",
            "second:dragging position=0 drag=10",
            "first:committed position=10 drag=10",
            "second:committed position=10 drag=10"
        ), events);
    }

    @Test
    @DisplayName("Should notify each unique node listener once per movement event")
    void shouldNotifyEachUniqueNodeListenerOncePerMovementEvent() {
        Node node = new Node(new Point2D.Double(0, 0));
        AtomicInteger notificationCount = new AtomicInteger();
        NodeListener listener = movedNode -> notificationCount.incrementAndGet();

        node.addListener(null);
        node.addListener(listener);
        node.addListener(listener);

        node.startDragging();
        node.updateDragging(new Point2D.Double(10, 0));

        assertEquals(1, notificationCount.get());
    }

    @Test
    @DisplayName("Should stop notifying removed node listeners")
    void shouldStopNotifyingRemovedNodeListeners() {
        Node node = new Node(new Point2D.Double(0, 0));
        AtomicInteger activeListenerNotifications = new AtomicInteger();
        AtomicInteger removedListenerNotifications = new AtomicInteger();
        NodeListener activeListener = movedNode -> activeListenerNotifications.incrementAndGet();
        NodeListener removedListener = movedNode -> removedListenerNotifications.incrementAndGet();

        node.addListener(activeListener);
        node.addListener(removedListener);
        node.removeListener(removedListener);

        node.startDragging();
        node.updateDragging(new Point2D.Double(10, 0));
        node.stopDragging();

        assertEquals(2, activeListenerNotifications.get());
        assertEquals(0, removedListenerNotifications.get());
    }

    @Test
    @DisplayName("Should pass the moved node instance to listeners")
    void shouldPassMovedNodeInstanceToListeners() {
        Node node = new Node(new Point2D.Double(0, 0));
        List<Node> notifiedNodes = new ArrayList<>();

        node.addListener(notifiedNodes::add);

        node.startDragging();
        node.updateDragging(new Point2D.Double(10, 0));

        assertEquals(1, notifiedNodes.size());
        assertSame(node, notifiedNodes.get(0));
    }

    @Test
    @DisplayName("Should not notify node listeners when drag update is ignored")
    void shouldNotNotifyNodeListenersWhenDragUpdateIsIgnored() {
        Node node = new Node(new Point2D.Double(0, 0));
        AtomicInteger notificationCount = new AtomicInteger();

        node.addListener(movedNode -> notificationCount.incrementAndGet());

        node.updateDragging(new Point2D.Double(10, 0));
        node.startDragging();
        node.updateDragging(null);
        node.stopDragging();

        assertEquals(0, notificationCount.get());
    }

    private String describe(Node node) {
        return String.format(
            "%s position=%.0f drag=%.0f",
            node.isDragging() ? "dragging" : "committed",
            node.getPosition().getX(),
            node.getDragPosition().getX()
        );
    }
}

package unit;

import model.game.MoveTransaction;
import model.listeners.TransactionListener;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TransactionListener Tests")
class TransactionListenerTest {

    @Test
    @DisplayName("Should receive committed node and final position")
    void shouldReceiveCommittedNodeAndPosition() {
        Node node = new Node(new Point2D.Double(1, 1), true);
        RecordingListener listener = new RecordingListener();
        MoveTransaction transaction = new MoveTransaction(node, listener);

        transaction.startDragging();
        transaction.updateDragging(new Point2D.Double(25, 30));
        transaction.stopDragging();

        assertEquals(1, listener.calls.get());
        assertSame(node, listener.node.get());
        assertEquals(new Point2D.Double(25, 30), listener.position.get());
    }

    @Test
    @DisplayName("Should not be called when transaction is cancelled")
    void shouldNotBeCalledOnCancel() {
        Node node = new Node(new Point2D.Double(1, 1), true);
        RecordingListener listener = new RecordingListener();
        MoveTransaction transaction = new MoveTransaction(node, listener);

        transaction.startDragging();
        transaction.updateDragging(new Point2D.Double(25, 30));
        transaction.cancelDragging();
        transaction.stopDragging();

        assertEquals(0, listener.calls.get());
    }

    private static class RecordingListener implements TransactionListener {
        final AtomicInteger calls = new AtomicInteger(0);
        final AtomicReference<Node> node = new AtomicReference<>();
        final AtomicReference<Point2D> position = new AtomicReference<>();

        @Override
        public void onCommitted(Node node, Point2D finalPosition) {
            calls.incrementAndGet();
            this.node.set(node);
            this.position.set(finalPosition);
        }
    }
}

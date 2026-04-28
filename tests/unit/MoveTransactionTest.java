package unit;

import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MoveTransaction Unit Tests")
class MoveTransactionTest {

    @Nested
    @DisplayName("Lifecycle")
    class LifecycleTests {

        @Test
        @DisplayName("Should commit the final dragged position to the listener")
        void shouldCommitFinalPosition() {
            Node node = new Node(new Point2D.Double(10, 10));
            RecordingListener listener = new RecordingListener();
            MoveTransaction transaction = new MoveTransaction(node, listener);

            transaction.startDragging();
            transaction.updateDragging(new Point2D.Double(20, 20));
            transaction.updateDragging(new Point2D.Double(30, 30));
            transaction.stopDragging();

            assertSame(node, listener.node.get());
            assertEquals(new Point2D.Double(30, 30), listener.position.get());
            assertEquals(1, listener.commitCount.get());
        }

        @Test
        @DisplayName("Should use the node position when no drag updates were recorded")
        void shouldCommitCurrentNodePositionWhenQueueEmpty() {
            Node node = new Node(new Point2D.Double(10, 10));
            RecordingListener listener = new RecordingListener();
            MoveTransaction transaction = new MoveTransaction(node, listener);

            transaction.startDragging();
            transaction.stopDragging();

            assertEquals(new Point2D.Double(10, 10), listener.position.get());
        }

        @Test
        @DisplayName("Should ignore updates before dragging starts")
        void shouldIgnoreUpdatesBeforeStart() {
            Node node = new Node(new Point2D.Double(10, 10));
            RecordingListener listener = new RecordingListener();
            MoveTransaction transaction = new MoveTransaction(node, listener);

            transaction.updateDragging(new Point2D.Double(20, 20));
            transaction.stopDragging();

            assertEquals(0, listener.commitCount.get());
        }

        @Test
        @DisplayName("Should cancel without notifying the listener")
        void shouldCancelWithoutCommit() {
            Node node = new Node(new Point2D.Double(10, 10));
            RecordingListener listener = new RecordingListener();
            MoveTransaction transaction = new MoveTransaction(node, listener);

            transaction.startDragging();
            transaction.updateDragging(new Point2D.Double(20, 20));
            transaction.cancelDragging();
            transaction.stopDragging();

            assertEquals(0, listener.commitCount.get());
        }

        @Test
        @DisplayName("Should expose the wrapped node")
        void shouldReturnNode() {
            Node node = new Node(new Point2D.Double(10, 10));
            MoveTransaction transaction = new MoveTransaction(node, null);

            assertSame(node, transaction.getNode());
        }
    }

    private static class RecordingListener implements TransactionListener {
        final AtomicInteger commitCount = new AtomicInteger(0);
        final AtomicReference<Node> node = new AtomicReference<>();
        final AtomicReference<Point2D> position = new AtomicReference<>();

        @Override
        public void onCommitted(Node node, Point2D finalPosition) {
            commitCount.incrementAndGet();
            this.node.set(node);
            this.position.set(finalPosition);
        }
    }
}

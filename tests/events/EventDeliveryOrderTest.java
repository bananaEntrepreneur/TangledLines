package events;

import model.game.Field;
import model.game.state.GameState;
import model.game.state.LevelNavigation;
import model.level.Level;
import model.level.LevelManager;
import model.level.seeder.Seeder;
import model.listeners.LevelNavigationListener;
import model.listeners.ListenerPriority;
import model.listeners.NodeListener;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Event Delivery Order Tests")
class EventDeliveryOrderTest {

    @Test
    @DisplayName("Should deliver node movement events to model before view")
    void shouldDeliverNodeMovementEventsToModelBeforeView() {
        Node node = new Node(new Point2D.Double(0, 0));
        List<String> events = new ArrayList<>();

        node.addListener(nodeListener("view", ListenerPriority.MEDIUM, events));
        node.addListener(nodeListener("model", ListenerPriority.HIGH, events));

        node.startDragging();
        node.updateDragging(new Point2D.Double(10, 0));
        node.stopDragging();

        assertEquals(List.of(
            "model:dragging",
            "view:dragging",
            "model:committed",
            "view:committed"
        ), events);
    }

    @Test
    @DisplayName("Should deliver level navigation events to model before view")
    void shouldDeliverLevelNavigationEventsToModelBeforeView() {
        GameState state = new GameState(new Field(), 3);
        LevelNavigation navigation = new LevelNavigation(new LevelManager(new TwoLevelSeeder()), state);
        List<String> events = new ArrayList<>();

        navigation.addListener(levelNavigationListener("view", ListenerPriority.LOW, events));
        navigation.addListener(levelNavigationListener("model", ListenerPriority.MEDIUM, events));

        state.win();

        assertTrue(navigation.nextLevel());
        assertEquals(List.of("model:index=1", "view:index=1"), events);
    }

    private NodeListener nodeListener(
            String name,
            ListenerPriority priority,
            List<String> events
    ) {
        return new NodeListener() {
            @Override
            public void onMoved(Node node) {
                events.add(name + ":" + (node.isDragging() ? "dragging" : "committed"));
            }

            @Override
            public ListenerPriority getPriority() {
                return priority;
            }
        };
    }

    private LevelNavigationListener levelNavigationListener(
            String name,
            ListenerPriority priority,
            List<String> events
    ) {
        return new LevelNavigationListener() {
            @Override
            public void onLevelChanged(LevelNavigation levelNavigation) {
                events.add(name + ":index=" + levelNavigation.getCurrentLevelIndex());
            }

            @Override
            public ListenerPriority getPriority() {
                return priority;
            }
        };
    }

    private static class TwoLevelSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(
                singleNodeLevel(3),
                singleNodeLevel(4)
            );
        }

        private Level singleNodeLevel(int maxMoves) {
            return level(maxMoves, () -> {
                Field field = field();
                node(field, 0, 0);
                return field;
            });
        }
    }
}

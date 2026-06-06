package events;

import model.game.Field;
import model.game.Game;
import model.game.GameState;
import model.game.LevelNavigation;
import model.level.Level;
import model.level.LevelManager;
import model.level.seeder.Seeder;
import model.listeners.LevelNavigationListener;
import model.listeners.Priority;
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

        node.addListener(nodeListener("view", Priority.MEDIUM, events));
        node.addListener(nodeListener("model", Priority.HIGH, events));

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
    @DisplayName("Should deliver node listeners by priority regardless of registration order")
    void shouldDeliverNodeListenersByPriority() {
        Node node = new Node(new Point2D.Double(0, 0));
        List<String> events = new ArrayList<>();

        node.addListener(nodeListener("view", Priority.LOW, events));
        node.addListener(nodeListener("rules", Priority.HIGH, events));
        node.addListener(nodeListener("panel", Priority.MEDIUM, events));

        node.startDragging();
        node.updateDragging(new Point2D.Double(10, 0));

        assertEquals(List.of(
            "rules:dragging",
            "panel:dragging",
            "view:dragging"
        ), events);
    }

    @Test
    @DisplayName("Should execute node listener body before calling the next listener")
    void shouldExecuteNodeListenerBodyBeforeCallingNextListener() {
        Node node = new Node(new Point2D.Double(0, 0));
        List<String> events = new ArrayList<>();
        int[] step = {0};

        node.addListener(nodeExecutionListener("view", Priority.LOW, events, step, 2, 3));
        node.addListener(nodeExecutionListener("panel", Priority.MEDIUM, events, step, 1, 2));
        node.addListener(nodeExecutionListener("rules", Priority.HIGH, events, step, 0, 1));

        node.startDragging();
        node.updateDragging(new Point2D.Double(10, 0));

        assertEquals(List.of(
            "rules:before=0 dragging=true",
            "panel:before=1 dragging=true",
            "view:before=2 dragging=true"
        ), events);
        assertEquals(3, step[0]);
    }

    @Test
    @DisplayName("Should let game update state before view observes committed node movement")
    void shouldLetGameUpdateStateBeforeViewObservesCommittedNodeMovement() {
        Game game = new Game(new LevelManager(new CrossingLevelSeeder()));
        GameState state = game.getState();
        Node node = state.getField().getNodes().get(2);
        List<String> events = new ArrayList<>();

        node.addListener(new NodeListener() {
            @Override
            public void onMoved(Node movedNode) {
                events.add((movedNode.isDragging() ? "view:dragging" : "view:committed")
                    + " moves=" + state.getMoveCount()
                    + " win=" + state.isCurrentLevelWon());
            }

            @Override
            public Priority getPriority() {
                return Priority.MEDIUM;
            }
        });

        node.startDragging();
        node.updateDragging(new Point2D.Double(0, -100));
        node.stopDragging();

        assertEquals(List.of(
            "view:dragging moves=0 win=false",
            "view:committed moves=1 win=true"
        ), events);
    }

    @Test
    @DisplayName("Should deliver level navigation events to model before view")
    void shouldDeliverLevelNavigationEventsToModelBeforeView() {
        Game game = new Game(new LevelManager(new TwoLevelSeeder()));
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        navigation.addListener(levelNavigationListener("view", Priority.LOW, events));
        navigation.addListener(levelNavigationListener("model", Priority.MEDIUM, events));

        winCrossingLevel(state);

        assertTrue(navigation.nextLevel());
        assertEquals(List.of("model:index=1", "view:index=1"), events);
    }

    @Test
    @DisplayName("Should deliver level navigation listeners by priority")
    void shouldDeliverLevelNavigationListenersByPriority() {
        Game game = new Game(new LevelManager(new TwoLevelSeeder()));
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        navigation.addListener(levelNavigationListener("view", Priority.LOW, events));
        navigation.addListener(levelNavigationListener("rules", Priority.HIGH, events));
        navigation.addListener(levelNavigationListener("model", Priority.MEDIUM, events));

        winCrossingLevel(state);

        assertTrue(navigation.nextLevel());
        assertEquals(List.of("rules:index=1", "model:index=1", "view:index=1"), events);
    }

    @Test
    @DisplayName("Should deliver game state listeners by priority")
    void shouldDeliverGameStateListenersByPriority() {
        Game game = new Game(new LevelManager(new CrossingLevelSeeder()));
        GameState state = game.getState();
        Node node = state.getField().getNodes().get(0);
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener("view", Priority.LOW, events));
        state.addListener(gameStateListener("rules", Priority.HIGH, events));
        state.addListener(gameStateListener("model", Priority.MEDIUM, events));

        drag(node, 10, 0);

        assertEquals(List.of("rules:moves=1", "model:moves=1", "view:moves=1"), events);
    }

    @Test
    @DisplayName("Should execute game state listener body before calling the next listener")
    void shouldExecuteGameStateListenerBodyBeforeCallingNextListener() {
        Game game = new Game(new LevelManager(new CrossingLevelSeeder()));
        GameState state = game.getState();
        Node node = state.getField().getNodes().get(0);
        List<String> events = new ArrayList<>();
        int[] step = {0};

        state.addListener(gameStateExecutionListener("view", Priority.LOW, events, step, 2, 3));
        state.addListener(gameStateExecutionListener("model", Priority.MEDIUM, events, step, 1, 2));
        state.addListener(gameStateExecutionListener("rules", Priority.HIGH, events, step, 0, 1));

        drag(node, 10, 0);

        assertEquals(List.of(
            "rules:before=0 moves=1",
            "model:before=1 moves=1",
            "view:before=2 moves=1"
        ), events);
        assertEquals(3, step[0]);
    }

    @Test
    @DisplayName("Should execute level navigation listener body before calling the next listener")
    void shouldExecuteLevelNavigationListenerBodyBeforeCallingNextListener() {
        Game game = new Game(new LevelManager(new TwoLevelSeeder()));
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();
        int[] step = {0};

        navigation.addListener(levelNavigationExecutionListener("view", Priority.LOW, events, step, 2, 3));
        navigation.addListener(levelNavigationExecutionListener("model", Priority.MEDIUM, events, step, 1, 2));
        navigation.addListener(levelNavigationExecutionListener("rules", Priority.HIGH, events, step, 0, 1));

        winCrossingLevel(state);
        assertTrue(navigation.nextLevel());

        assertEquals(List.of(
            "rules:before=0 index=1",
            "model:before=1 index=1",
            "view:before=2 index=1"
        ), events);
        assertEquals(3, step[0]);
    }

    private NodeListener nodeListener(
            String name,
            Priority priority,
            List<String> events
    ) {
        return new NodeListener() {
            @Override
            public void onMoved(Node node) {
                events.add(name + ":" + (node.isDragging() ? "dragging" : "committed"));
            }

            @Override
            public Priority getPriority() {
                return priority;
            }
        };
    }

    private NodeListener nodeExecutionListener(
            String name,
            Priority priority,
            List<String> events,
            int[] step,
            int expectedStep,
            int nextStep
    ) {
        return new NodeListener() {
            @Override
            public void onMoved(Node node) {
                events.add(name + ":before=" + step[0] + " dragging=" + node.isDragging());
                assertEquals(expectedStep, step[0]);
                step[0] = nextStep;
            }

            @Override
            public Priority getPriority() {
                return priority;
            }
        };
    }

    private LevelNavigationListener levelNavigationListener(
            String name,
            Priority priority,
            List<String> events
    ) {
        return new LevelNavigationListener() {
            @Override
            public void onLevelChanged(LevelNavigation levelNavigation) {
                events.add(name + ":index=" + levelNavigation.getCurrentLevelIndex());
            }

            @Override
            public Priority getPriority() {
                return priority;
            }
        };
    }

    private LevelNavigationListener levelNavigationExecutionListener(
            String name,
            Priority priority,
            List<String> events,
            int[] step,
            int expectedStep,
            int nextStep
    ) {
        return new LevelNavigationListener() {
            @Override
            public void onLevelChanged(LevelNavigation levelNavigation) {
                events.add(name + ":before=" + step[0] + " index=" + levelNavigation.getCurrentLevelIndex());
                assertEquals(expectedStep, step[0]);
                step[0] = nextStep;
            }

            @Override
            public Priority getPriority() {
                return priority;
            }
        };
    }

    private model.listeners.GameStateListener gameStateListener(
            String name,
            Priority priority,
            List<String> events
    ) {
        return new model.listeners.GameStateListener() {
            @Override
            public void onGameStateChanged(GameState gameState) {
                events.add(name + ":moves=" + gameState.getMoveCount());
            }

            @Override
            public Priority getPriority() {
                return priority;
            }
        };
    }

    private model.listeners.GameStateListener gameStateExecutionListener(
            String name,
            Priority priority,
            List<String> events,
            int[] step,
            int expectedStep,
            int nextStep
    ) {
        return new model.listeners.GameStateListener() {
            @Override
            public void onGameStateChanged(GameState gameState) {
                events.add(name + ":before=" + step[0] + " moves=" + gameState.getMoveCount());
                assertEquals(expectedStep, step[0]);
                step[0] = nextStep;
            }

            @Override
            public Priority getPriority() {
                return priority;
            }
        };
    }

    private static class TwoLevelSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(
                crossingLevel(3),
                createLevel(4, () -> {
                    Field field = createField();
                    createNode(field, 0, 0);
                    return field;
                })
            );
        }

        private Level crossingLevel(int maxMoves) {
            return createLevel(maxMoves, () -> {
                Field field = createField();
                Node a = createNode(field, 0, 0);
                Node b = createNode(field, 100, 100);
                Node c = createNode(field, 0, 100);
                Node d = createNode(field, 100, 0);
                createEdge(field, a, b);
                createEdge(field, c, d);
                return field;
            });
        }
    }

    private static class OneLevelSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(createLevel(3, () -> {
                Field field = createField();
                createNode(field, 0, 0);
                return field;
            }));
        }
    }

    private static class CrossingLevelSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(createLevel(3, () -> {
                Field field = createField();
                Node a = createNode(field, 0, 0);
                Node b = createNode(field, 100, 100);
                Node c = createNode(field, 0, 100);
                Node d = createNode(field, 100, 0);
                createEdge(field, a, b);
                createEdge(field, c, d);
                return field;
            }));
        }
    }

    private void winCrossingLevel(GameState state) {
        Node node = state.getField().getNodes().get(2);
        drag(node, 0, -100);
    }

    private void drag(Node node, double x, double y) {
        node.startDragging();
        node.updateDragging(new Point2D.Double(x, y));
        node.stopDragging();
    }
}

package events;

import model.game.Field;
import model.game.Game;
import model.game.GameState;
import model.game.LevelNavigation;
import model.level.Level;
import model.level.LevelManager;
import model.level.seeder.Seeder;
import model.listeners.GameStateListener;
import model.listeners.LevelNavigationListener;
import model.listeners.NodeListener;
import model.listeners.Priority;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Model Event Protocol Scenario Tests")
class ModelProtocolScenarioTest {

    @Test
    @DisplayName("Should publish node preview and commit events with model state after each command")
    void shouldPublishNodeMovementProtocolWithStateAfterEachCommand() {
        Node node = new Node(new Point2D.Double(0, 0));
        List<String> events = new ArrayList<>();

        node.addListener(new NodeListener() {
            @Override
            public void onMoved(Node movedNode) {
                assertSame(node, movedNode);
                events.add("NodeMoved "
                    + phaseOf(movedNode)
                    + " position=" + xOf(movedNode.getPosition())
                    + " drag=" + xOf(movedNode.getDragPosition()));
            }

            @Override
            public Priority getPriority() {
                return Priority.MEDIUM;
            }
        });

        node.startDragging();
        assertTrue(node.isDragging());
        assertEquals(new Point2D.Double(0, 0), node.getPosition());
        assertEquals(List.of(), events);

        node.updateDragging(new Point2D.Double(10, 0));
        assertTrue(node.isDragging());
        assertEquals(new Point2D.Double(0, 0), node.getPosition());
        assertEquals(new Point2D.Double(10, 0), node.getDragPosition());
        assertEquals(List.of("NodeMoved preview position=0 drag=10"), events);

        node.stopDragging();
        assertFalse(node.isDragging());
        assertEquals(new Point2D.Double(10, 0), node.getPosition());
        assertEquals(new Point2D.Double(10, 0), node.getDragPosition());
        assertEquals(List.of(
            "NodeMoved preview position=0 drag=10",
            "NodeMoved commit position=10 drag=10"
        ), events);
    }

    @Test
    @DisplayName("Should publish node and game-state events in order while resolving a level")
    void shouldPublishGameplayProtocolWithStateAfterEachEvent() {
        Game game = new Game(new LevelManager(new TwoLevelSeeder()));
        GameState state = game.getState();
        Node node = state.getField().getNodes().get(2);
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(changedState -> events.add("GameStateChanged "
            + stateSnapshot(changedState))));
        node.addListener(new NodeListener() {
            @Override
            public void onMoved(Node movedNode) {
                assertSame(node, movedNode);
                events.add("NodeMoved "
                    + phaseOf(movedNode)
                    + " position=" + pointOf(movedNode.getPosition())
                    + " drag=" + pointOf(movedNode.getDragPosition())
                    + " " + stateSnapshot(state)
                    + " intersections=" + state.getField().hasIntersections());
            }

            @Override
            public Priority getPriority() {
                return Priority.MEDIUM;
            }
        });

        node.startDragging();
        assertTrue(node.isDragging());
        assertEquals(0, state.getMoveCount());
        assertFalse(state.isCurrentLevelFinished());
        assertEquals(List.of(), events);

        node.updateDragging(new Point2D.Double(0, -100));
        assertTrue(node.isDragging());
        assertEquals(0, state.getMoveCount());
        assertFalse(state.isCurrentLevelFinished());
        assertFalse(state.getField().hasIntersections());
        assertEquals(List.of(
            "NodeMoved preview position=(0,100) drag=(0,-100) moves=0 max=3 finished=false won=false allComplete=false intersections=false"
        ), events);

        node.stopDragging();
        assertFalse(node.isDragging());
        assertEquals(1, state.getMoveCount());
        assertTrue(state.isCurrentLevelFinished());
        assertTrue(state.isCurrentLevelWon());
        assertFalse(state.isAllLevelsComplete());
        assertEquals(List.of(
            "NodeMoved preview position=(0,100) drag=(0,-100) moves=0 max=3 finished=false won=false allComplete=false intersections=false",
            "GameStateChanged moves=1 max=3 finished=false won=false allComplete=false",
            "GameStateChanged moves=1 max=3 finished=true won=true allComplete=false",
            "NodeMoved commit position=(0,-100) drag=(0,-100) moves=1 max=3 finished=true won=true allComplete=false intersections=false"
        ), events);
    }

    @Test
    @DisplayName("Should publish level events only after state changes and skip them on all-level completion")
    void shouldPublishLevelNavigationProtocolWithStateAfterEachCommand() {
        Game game = new Game(new LevelManager(new TwoLevelSeeder()));
        GameState state = game.getState();
        LevelNavigation navigation = game.getLevelNavigation();
        List<String> events = new ArrayList<>();

        state.addListener(gameStateListener(changedState -> events.add("GameStateChanged "
            + "index=" + navigation.getCurrentLevelIndex()
            + " nodes=" + changedState.getField().getNodes().size()
            + " " + stateSnapshot(changedState))));
        navigation.addListener(new LevelNavigationListener() {
            @Override
            public void onLevelChanged(LevelNavigation changedNavigation) {
                assertSame(navigation, changedNavigation);
                events.add("LevelChanged "
                    + "index=" + changedNavigation.getCurrentLevelIndex()
                    + " total=" + changedNavigation.getTotalLevelCount()
                    + " nodes=" + state.getField().getNodes().size()
                    + " " + stateSnapshot(state));
            }

            @Override
            public Priority getPriority() {
                return Priority.MEDIUM;
            }
        });

        assertFalse(navigation.nextLevel());
        assertEquals(0, navigation.getCurrentLevelIndex());
        assertEquals(3, state.getMaxMoveCount());
        assertEquals(List.of(), events);

        winCrossingLevel(state);
        events.clear();

        assertTrue(navigation.nextLevel());
        assertEquals(1, navigation.getCurrentLevelIndex());
        assertEquals(4, state.getMaxMoveCount());
        assertEquals(0, state.getMoveCount());
        assertFalse(state.isCurrentLevelFinished());
        assertEquals(List.of(
            "GameStateChanged index=1 nodes=1 moves=0 max=4 finished=false won=false allComplete=false",
            "LevelChanged index=1 total=2 nodes=1 moves=0 max=4 finished=false won=false allComplete=false"
        ), events);

        move(state.getField().getNodes().get(0), 50, 0);
        assertTrue(state.isCurrentLevelWon());
        events.clear();

        assertFalse(navigation.nextLevel());
        assertTrue(state.isAllLevelsComplete());
        assertEquals(1, navigation.getCurrentLevelIndex());
        assertEquals(List.of(
            "GameStateChanged index=1 nodes=1 moves=1 max=4 finished=false won=false allComplete=true"
        ), events);
    }

    private GameStateListener gameStateListener(Consumer<GameState> recorder) {
        return new GameStateListener() {
            @Override
            public void onGameStateChanged(GameState gameState) {
                recorder.accept(gameState);
            }

            @Override
            public Priority getPriority() {
                return Priority.MEDIUM;
            }
        };
    }

    private void winCrossingLevel(GameState state) {
        move(state.getField().getNodes().get(2), 0, -100);
        assertTrue(state.isCurrentLevelWon());
    }

    private void move(Node node, double x, double y) {
        node.startDragging();
        node.updateDragging(new Point2D.Double(x, y));
        node.stopDragging();
    }

    private String phaseOf(Node node) {
        return node.isDragging() ? "preview" : "commit";
    }

    private String stateSnapshot(GameState state) {
        return "moves=" + state.getMoveCount()
            + " max=" + state.getMaxMoveCount()
            + " finished=" + state.isCurrentLevelFinished()
            + " won=" + state.isCurrentLevelWon()
            + " allComplete=" + state.isAllLevelsComplete();
    }

    private String pointOf(Point2D point) {
        return "(" + xOf(point) + "," + yOf(point) + ")";
    }

    private String xOf(Point2D point) {
        return String.format("%.0f", point.getX());
    }

    private String yOf(Point2D point) {
        return String.format("%.0f", point.getY());
    }

    private static class TwoLevelSeeder extends Seeder {
        @Override
        public List<Level> seed() {
            return List.of(
                createLevel(3, () -> {
                    Field field = createField();
                    Node a = createNode(field, 0, 0);
                    Node b = createNode(field, 100, 100);
                    Node c = createNode(field, 0, 100);
                    Node d = createNode(field, 100, 0);
                    createEdge(field, a, b);
                    createEdge(field, c, d);
                    return field;
                }),
                createLevel(4, () -> {
                    Field field = createField();
                    createNode(field, 0, 0);
                    return field;
                })
            );
        }
    }
}

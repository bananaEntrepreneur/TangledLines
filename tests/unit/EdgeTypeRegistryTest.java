package unit;

import model.game.Field;
import model.level.EdgeTypeFactory;
import model.level.EdgeTypeRegistry;
import model.level.Level;
import model.level.LevelFactory;
import model.units.Edge;
import model.units.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("Edge Type Registry Tests")
class EdgeTypeRegistryTest {

    @Test
    @DisplayName("Should create custom edge type through registered factory")
    void shouldCreateCustomEdgeTypeThroughRegisteredFactory() {
        EdgeTypeRegistry registry = EdgeTypeRegistry.withDefaults()
            .register(new ElasticEdgeFactory());

        Level level = new Level(
            3,
            List.of(
                new Level.NodeData(0, 0),
                new Level.NodeData(100, 0)
            ),
            List.of(new Level.EdgeSpec(
                0,
                1,
                "elastic",
                Map.of("elasticity", 2.0)
            ))
        );

        Field field = new LevelFactory(registry).createField(level);

        assertEquals(1, field.getEdges().size());
        assertInstanceOf(ElasticEdge.class, field.getEdges().get(0));
        assertEquals(2.0, ((ElasticEdge) field.getEdges().get(0)).getElasticity(), 0.01);
    }

    private static class ElasticEdgeFactory implements EdgeTypeFactory {
        @Override
        public String getType() {
            return "elastic";
        }

        @Override
        public Edge createEdge(Field field, Node nodeA, Node nodeB, Level.EdgeSpec spec) {
            double elasticity = spec.parameters().getCustomOrDefault("elasticity", 1.0);
            return field.addEdge(new ElasticEdge(nodeA, nodeB, elasticity));
        }
    }

    private static class ElasticEdge extends Edge {
        private final double _elasticity;

        ElasticEdge(Node nodeA, Node nodeB, double elasticity) {
            super(nodeA, nodeB);
            _elasticity = elasticity;
        }

        double getElasticity() {
            return _elasticity;
        }
    }
}

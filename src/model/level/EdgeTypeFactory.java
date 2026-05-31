package model.level;

import model.game.Field;
import model.units.Edge;
import model.units.Node;

public interface EdgeTypeFactory {
    String getType();

    Edge createEdge(Field field, Node nodeA, Node nodeB, Level.EdgeSpec spec);
}

import model.game.DefaultIntersectionChecker;
import model.game.Field;
import model.game.Game;
import model.units.Edge;
import model.units.Node;
import view.View;

import java.awt.geom.Point2D;

public class GameLauncher {
    public static void main(String[] args) {
        Field field = createSampleField();
        Game game = new Game(field, 50, new DefaultIntersectionChecker());
        
        View view = new View(game);
        view.show();
    }

    private static Field createSampleField() {
        Field field = new Field();

        // Create nodes in a tangled configuration
        Node n1 = new Node(new Point2D.Double(200, 100), true);
        Node n2 = new Node(new Point2D.Double(600, 100), true);
        Node n3 = new Node(new Point2D.Double(200, 500), true);
        Node n4 = new Node(new Point2D.Double(600, 500), true);

        // Create crossing edges (tangled)
        Edge e1 = new Edge(n1, n4);  // diagonal
        Edge e2 = new Edge(n2, n3);  // crosses e1
        Edge e3 = new Edge(n1, n2);  // top
        Edge e4 = new Edge(n3, n4);  // bottom
        Edge e5 = new Edge(n1, n3);  // left
        Edge e6 = new Edge(n2, n4);  // right

        field.addNode(n1);
        field.addNode(n2);
        field.addNode(n3);
        field.addNode(n4);

        field.addEdge(e1);
        field.addEdge(e2);
        field.addEdge(e3);
        field.addEdge(e4);
        field.addEdge(e5);
        field.addEdge(e6);

        return field;
    }
}

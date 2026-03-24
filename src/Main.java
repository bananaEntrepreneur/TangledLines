import model.game.Field;
import model.game.Game;
import model.game.DefaultIntersectionChecker;
import view.View;

import java.awt.geom.Point2D;

public class Main {
    public static void main(String[] args) {
        Field field = createSampleField();
        Game game = new Game(field, 50, new DefaultIntersectionChecker());

        View view = new View(game);
        view.show();
    }

    private static Field createSampleField() {
        Field field = new Field();

        var n1 = field.createNode(new Point2D.Double(200, 100), true);
        var n2 = field.createNode(new Point2D.Double(600, 100), true);
        var n3 = field.createNode(new Point2D.Double(200, 500), false);
        var n4 = field.createNode(new Point2D.Double(600, 500), true);

        field.createEdge(n1, n4);
        field.createEdge(n2, n3);
        field.createEdge(n1, n2);
        field.createEdge(n3, n4);
        field.createEdge(n1, n3);
        field.createEdge(n2, n4);

        return field;
    }
}

package model.level.seeder;

import model.game.Field;
import model.level.Level;
import model.units.Node;

import java.util.List;

public class SimpleSeeder extends Seeder {
    @Override
    public List<Level> seed() {
        return List.of(
            firstLevel(),
            secondLevel(),
            thirdLevel(),
            fourthLevel(),
            fifthLevel(),
            sixthLevel(),
            seventhLevel(),
            eighthLevel()
        );
    }

    private Level firstLevel() {
        return createLevel(3, () -> {
            Field field = createField();

            Node a = createNode(field, 200, 100);
            Node b = createNode(field, 600, 100);
            Node c = createNode(field, 200, 500);
            Node d = createNode(field, 600, 500);

            createEdge(field, a, d);
            createEdge(field, b, c);
            createEdge(field, a, b);
            createEdge(field, c, d);
            createEdge(field, a, c);
            createEdge(field, b, d);

            return field;
        });
    }

    private Level secondLevel() {
        return createLevel(4, () -> {
            Field field = createField();

            Node a = createNode(field, 200, 100);
            Node b = createNode(field, 600, 100);
            Node c = createNode(field, 200, 400);
            Node d = createNode(field, 600, 400);

            createStretchableEdge(field, a, b, 20);
            createStretchableEdge(field, c, d, 20);
            createEdge(field, a, d);
            createEdge(field, b, c);

            return field;
        });
    }

    private Level thirdLevel() {
        return createLevel(3, () -> {
            Field field = createField();

            Node a = createNode(field, 300, 100);
            Node b = createNode(field, 500, 100);
            Node c = createNode(field, 300, 350);
            Node d = createNode(field, 500, 350);

            createBreakableEdge(field, a, b, 80);
            createBreakableEdge(field, c, d, 80);
            createEdge(field, a, d);
            createEdge(field, b, c);

            return field;
        });
    }

    private Level fourthLevel() {
        return createLevel(5, () -> {
            Field field = createField();

            Node a = createNode(field, 150, 150);
            Node b = createNode(field, 650, 150);
            Node c = createNode(field, 150, 400);
            Node d = createNode(field, 650, 400);
            Node e = createNode(field, 400, 80);

            createStretchableEdge(field, a, e, 25);
            createStretchableEdge(field, b, e, 25);
            createEdge(field, a, b);
            createEdge(field, a, c);
            createEdge(field, b, d);
            createEdge(field, c, d);
            createEdge(field, c, e);
            createEdge(field, d, e);

            return field;
        });
    }

    private Level fifthLevel() {
        return createLevel(4, () -> {
            Field field = createField();

            Node a = createNode(field, 150, 150);
            Node b = createNode(field, 600, 200);
            Node c = createNode(field, 200, 450);
            Node d = createNode(field, 650, 400);
            Node e = createNode(field, 400, 100);

            createBreakableEdge(field, a, b, 20);
            createBreakableEdge(field, c, d, 20);
            createBreakableEdge(field, a, d, 20);
            createBreakableEdge(field, b, c, 20);
            createEdge(field, a, e);
            createEdge(field, b, e);
            createEdge(field, c, e);
            createEdge(field, d, e);

            return field;
        });
    }

    private Level sixthLevel() {
        return createLevel(5, () -> {
            Field field = createField();

            Node a = createNode(field, 100, 150);
            Node b = createNode(field, 400, 100);
            Node c = createNode(field, 700, 150);
            Node d = createNode(field, 100, 400);
            Node e = createNode(field, 400, 350);
            Node f = createNode(field, 700, 400);

            createStretchableEdge(field, a, d, 50);
            createStretchableEdge(field, b, e, 50);
            createStretchableEdge(field, c, f, 50);
            createEdge(field, a, b);
            createEdge(field, b, c);
            createEdge(field, d, e);
            createEdge(field, e, f);
            createEdge(field, a, e);
            createEdge(field, b, d);
            createEdge(field, b, f);
            createEdge(field, c, e);

            return field;
        });
    }

    private Level seventhLevel() {
        return createLevel(4, () -> {
            Field field = createField();

            Node a = createNode(field, 200, 120);
            Node b = createNode(field, 500, 120);
            Node c = createNode(field, 200, 300);
            Node d = createNode(field, 500, 300);
            Node e = createNode(field, 350, 400);

            createBreakableEdge(field, a, b, 50);
            createBreakableEdge(field, c, d, 50);
            createStretchableEdge(field, a, c, 35);
            createStretchableEdge(field, b, d, 35);
            createEdge(field, a, e);
            createEdge(field, b, e);
            createEdge(field, c, e);
            createEdge(field, d, e);

            return field;
        });
    }

    private Level eighthLevel() {
        return createLevel(6, () -> {
            Field field = createField();

            Node a = createNode(field, 120, 120);
            Node b = createNode(field, 420, 420);
            Node c = createNode(field, 120, 420);
            Node d = createNode(field, 420, 120);

            createOverheatingEdge(field, a, b, 30, 15, 100);
            createEdge(field, c, d);

            return field;
        });
    }
}

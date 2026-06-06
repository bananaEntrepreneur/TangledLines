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
            eighthLevel(),
            ninthLevel()
        );
    }

    private Level firstLevel() {
        return level(3, () -> {
            Field field = field();

            Node a = node(field, 200, 100);
            Node b = node(field, 600, 100);
            Node c = node(field, 200, 500);
            Node d = node(field, 600, 500);

            edge(field, a, d);
            edge(field, b, c);
            edge(field, a, b);
            edge(field, c, d);
            edge(field, a, c);
            edge(field, b, d);

            return field;
        });
    }

    private Level secondLevel() {
        return level(4, () -> {
            Field field = field();

            Node a = node(field, 200, 100);
            Node b = node(field, 600, 100);
            Node c = node(field, 200, 400);
            Node d = node(field, 600, 400);

            stretchableEdge(field, a, b, 20);
            stretchableEdge(field, c, d, 20);
            edge(field, a, d);
            edge(field, b, c);

            return field;
        });
    }

    private Level thirdLevel() {
        return level(3, () -> {
            Field field = field();

            Node a = node(field, 300, 100);
            Node b = node(field, 500, 100);
            Node c = node(field, 300, 350);
            Node d = node(field, 500, 350);

            breakableEdge(field, a, b, 80);
            breakableEdge(field, c, d, 80);
            edge(field, a, d);
            edge(field, b, c);

            return field;
        });
    }

    private Level fourthLevel() {
        return level(5, () -> {
            Field field = field();

            Node a = node(field, 150, 150);
            Node b = node(field, 650, 150);
            Node c = node(field, 150, 400);
            Node d = node(field, 650, 400);
            Node e = node(field, 400, 80);

            stretchableEdge(field, a, e, 25);
            stretchableEdge(field, b, e, 25);
            edge(field, a, b);
            edge(field, a, c);
            edge(field, b, d);
            edge(field, c, d);
            edge(field, c, e);
            edge(field, d, e);

            return field;
        });
    }

    private Level fifthLevel() {
        return level(4, () -> {
            Field field = field();

            Node a = node(field, 150, 150);
            Node b = node(field, 600, 200);
            Node c = node(field, 200, 450);
            Node d = node(field, 650, 400);
            Node e = node(field, 400, 100);

            breakableEdge(field, a, b, 20);
            breakableEdge(field, c, d, 20);
            breakableEdge(field, a, d, 20);
            breakableEdge(field, b, c, 20);
            edge(field, a, e);
            edge(field, b, e);
            edge(field, c, e);
            edge(field, d, e);

            return field;
        });
    }

    private Level sixthLevel() {
        return level(5, () -> {
            Field field = field();

            Node a = node(field, 100, 150);
            Node b = node(field, 400, 100);
            Node c = node(field, 700, 150);
            Node d = node(field, 100, 400);
            Node e = node(field, 400, 350);
            Node f = node(field, 700, 400);

            stretchableEdge(field, a, d, 50);
            stretchableEdge(field, b, e, 50);
            stretchableEdge(field, c, f, 50);
            edge(field, a, b);
            edge(field, b, c);
            edge(field, d, e);
            edge(field, e, f);
            edge(field, a, e);
            edge(field, b, d);
            edge(field, b, f);
            edge(field, c, e);

            return field;
        });
    }

    private Level seventhLevel() {
        return level(4, () -> {
            Field field = field();

            Node a = node(field, 200, 120);
            Node b = node(field, 500, 120);
            Node c = node(field, 200, 300);
            Node d = node(field, 500, 300);
            Node e = node(field, 350, 400);

            breakableEdge(field, a, b, 50);
            breakableEdge(field, c, d, 50);
            stretchableEdge(field, a, c, 35);
            stretchableEdge(field, b, d, 35);
            edge(field, a, e);
            edge(field, b, e);
            edge(field, c, e);
            edge(field, d, e);

            return field;
        });
    }

    private Level eighthLevel() {
        return level(6, () -> {
            Field field = field();

            Node a = node(field, 120, 120);
            Node b = node(field, 420, 420);
            Node c = node(field, 120, 420);
            Node d = node(field, 420, 120);

            overheatingEdge(field, a, b, 30, 15, 100);
            edge(field, c, d);

            return field;
        });
    }

    private Level ninthLevel() {
        return level(6, () -> {
            Field field = field();

            Node a = node(field, 100, 100);
            Node b = node(field, 700, 100);
            Node c = node(field, 100, 500);
            Node d = node(field, 700, 500);
            Node e = node(field, 400, 300);

            edge(field, a, b);
            edge(field, c, d);
            edge(field, a, c);
            edge(field, b, d);
            edge(field, a, d);
            edge(field, b, c);
            stretchableEdge(field, a, e, 40);
            stretchableEdge(field, b, e, 40);
            breakableEdge(field, c, e, 15);
            breakableEdge(field, d, e, 15);

            return field;
        });
    }
}

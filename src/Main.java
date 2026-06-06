import model.game.Game;
import model.level.LevelManager;
import model.level.seeder.SimpleSeeder;
import view.View;

public class Main {
    public static void main(String[] args) {
        LevelManager levelManager = new LevelManager(new SimpleSeeder());
        Game game = new Game(levelManager);
        View view = new View(game.getState(), game.getNavigation());
        view.show();
    }
}

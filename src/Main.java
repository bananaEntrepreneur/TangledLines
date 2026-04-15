import model.game.Field;
import model.game.Game;
import model.level.LevelManager;
import model.level.LevelLoadException;
import view.View;

public class Main {
    private static final String LEVELS_DIRECTORY = "levels";

    public static void main(String[] args) {
        LevelManager levelManager;
        try {
            levelManager = new LevelManager(LEVELS_DIRECTORY);
        } catch (LevelLoadException e) {
            System.err.println("Error loading levels: " + e.getMessage());
            return;
        }

        if (!levelManager.hasLevels()) {
            System.err.println("No levels found in " + LEVELS_DIRECTORY);
            return;
        }

        Game game = new Game(levelManager);

        View view = new View(game);
        view.show();
    }
}

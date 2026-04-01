package model.game.state;

public interface LevelNavigation {
    boolean nextLevel();
    void restartLevel();
    int getCurrentLevelIndex();
    int getTotalLevels();
    boolean hasNextLevel();
}

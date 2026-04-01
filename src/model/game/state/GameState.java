package model.game.state;

public interface GameState {
    boolean isGameOver();
    boolean isWin();
    boolean isAllLevelsComplete();
    int getMoveCount();
    int getMaxMoves();
    int getTotalLevels();
}

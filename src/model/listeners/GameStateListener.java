package model.listeners;

import model.game.GameState;

public interface GameStateListener extends Listener {
    void onGameStateChanged(GameState gameState);
}

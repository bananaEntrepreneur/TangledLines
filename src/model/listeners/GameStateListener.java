package model.listeners;

import model.game.state.GameState;

public interface GameStateListener extends Listener {
    void onGameStateChanged(GameState gameState);
}

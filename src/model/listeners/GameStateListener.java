package model.listeners;

import model.game.state.GameState;

public interface GameStateListener {
    void onGameStateChanged(GameState gameState);
}

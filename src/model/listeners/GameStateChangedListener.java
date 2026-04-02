package model.listeners;

import model.game.state.GameState;

public interface GameStateChangedListener {
    void onGameStateChanged(GameState gameState);
}

package model.listeners;

import model.game.state.LevelNavigation;

public interface LevelNavigationListener extends Listener {
    void onLevelChanged(LevelNavigation levelNavigation);
}

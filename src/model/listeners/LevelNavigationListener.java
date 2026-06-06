package model.listeners;

import model.game.LevelNavigation;

public interface LevelNavigationListener extends Listener {
    void onLevelChanged(LevelNavigation levelNavigation);
}

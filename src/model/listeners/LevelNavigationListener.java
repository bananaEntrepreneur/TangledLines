package model.listeners;

import model.game.state.LevelNavigation;

public interface LevelNavigationListener {
    void onLevelChanged(LevelNavigation levelNavigation);
}

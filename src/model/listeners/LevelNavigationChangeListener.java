package model.listeners;

import model.game.state.LevelNavigation;

public interface LevelNavigationChangeListener {
    void onLevelChanged(LevelNavigation levelNavigation);
}

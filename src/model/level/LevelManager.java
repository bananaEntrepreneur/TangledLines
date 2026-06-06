package model.level;

import model.game.Field;
import model.level.seeder.Seeder;

import java.util.List;

public class LevelManager {
    private final List<Level> _levels;
    private int _currentLevelIndex = 0;

    public LevelManager(Seeder seeder) {
        if (seeder == null) {
            throw new IllegalArgumentException("Seeder cannot be null");
        }
        _levels = seedLevels(seeder);
    }

    public Field createCurrentField() {
        if (_levels.isEmpty()) {
            throw new IllegalStateException("No levels available");
        }
        return _levels.get(_currentLevelIndex).createField();
    }

    public Field nextField() {
        if (!hasNextLevel()) {
            return null;
        }
        _currentLevelIndex++;
        return createCurrentField();
    }

    public int getCurrentMaxMoveCount() {
        if (_levels.isEmpty()) {
            throw new IllegalStateException("No levels available");
        }
        return _levels.get(_currentLevelIndex).getMaxMoveCount();
    }

    public int getTotalLevelCount() {
        return _levels.size();
    }

    public int getCurrentLevelIndex() {
        return _currentLevelIndex;
    }

    public boolean hasNextLevel() {
        return _currentLevelIndex < _levels.size() - 1;
    }

    private List<Level> seedLevels(Seeder seeder) {
        List<Level> levels = seeder.seed();
        if (levels == null) {
            throw new IllegalArgumentException("Seeder cannot return null");
        }
        for (Level level : levels) {
            if (level == null) {
                throw new IllegalArgumentException("Seeder cannot return null levels");
            }
        }
        return List.copyOf(levels);
    }
}

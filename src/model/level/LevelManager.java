package model.level;

import model.game.Field;
import model.level.factory.LevelFactory;
import model.level.loader.JsonLevelLoader;
import model.level.loader.LevelLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LevelManager {
    private final List<Level> _levels;
    private int _currentLevelIndex = 0;
    private final LevelFactory _factory;
    private final LevelLoader _levelLoader;

    public LevelManager(String levelsDirectory) throws LevelLoadException {
        this(levelsDirectory, new JsonLevelLoader());
    }

    public LevelManager(String levelsDirectory, LevelLoader levelLoader)
            throws LevelLoadException {
        _levelLoader = levelLoader;
        _factory = new LevelFactory();
        _levels = loadAllLevels(levelsDirectory);
    }

    public Field getCurrentField() {
        if (_levels.isEmpty()) {
            throw new IllegalStateException("No levels available");
        }
        return _factory.createField(_levels.get(_currentLevelIndex));
    }

    public Field nextField() {
        if (!hasNextLevel()) {
            return null;
        }
        _currentLevelIndex++;
        return getCurrentField();
    }

    public int getCurrentMaxMoves() {
        if (_levels.isEmpty()) {
            throw new IllegalStateException("No levels available");
        }
        return _levels.get(_currentLevelIndex).getMaxMoves();
    }

    public boolean hasLevels() {
        return !_levels.isEmpty();
    }

    public int getTotalLevels() {
        return _levels.size();
    }

    public int getCurrentLevelIndex() {
        return _currentLevelIndex;
    }

    public boolean hasNextLevel() {
        return _currentLevelIndex < _levels.size() - 1;
    }

    private List<Level> loadAllLevels(String directory) throws LevelLoadException {
        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        List<String> files = new ArrayList<>();
        File[] levelFiles = dir.listFiles((dirPath, name) -> name.endsWith(".json"));
        if (levelFiles != null) {
            Arrays.sort(levelFiles);
            for (File file : levelFiles) {
                files.add(file.getPath());
            }
        }

        List<Level> levels = new ArrayList<>();
        for (String path : files) {
            try {
                levels.add(_levelLoader.load(path));
            } catch (IOException e) {
                throw new LevelLoadException("Failed to load: " + path);
            }
        }
        return levels;
    }
}

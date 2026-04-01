package model.level.loader;

import model.level.Level;
import model.level.LevelLoadException;

import java.io.IOException;

public interface LevelLoader {
    Level load(String source) throws IOException, LevelLoadException;
}

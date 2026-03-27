package model.level;

import java.io.IOException;

public interface LevelLoader {
    Level load(String source) throws IOException, LevelLoadException;
}

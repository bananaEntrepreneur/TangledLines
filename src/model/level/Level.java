package model.level;

import model.game.Field;

import java.util.function.Supplier;

public class Level {
    private final int _maxMoveCount;
    private final Supplier<Field> _fieldSupplier;

    public Level(int maxMoveCount, Supplier<Field> fieldSupplier) {
        if (maxMoveCount < 1) {
            throw new IllegalArgumentException("maxMoveCount must be at least 1, got: " + maxMoveCount);
        }
        if (fieldSupplier == null) {
            throw new IllegalArgumentException("Field supplier cannot be null");
        }
        _maxMoveCount = maxMoveCount;
        _fieldSupplier = fieldSupplier;
    }

    public Field createField() {
        Field field = _fieldSupplier.get();
        if (field == null) {
            throw new IllegalStateException("Level field supplier cannot return null");
        }
        return field;
    }

    public int getMaxMoveCount() { return _maxMoveCount; }
}

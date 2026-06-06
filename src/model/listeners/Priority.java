package model.listeners;

public enum Priority {
    LOW(0),
    MEDIUM(50),
    HIGH(100);

    private final int _value;

    Priority(int value) {
        _value = value;
    }

    public int getValue() {
        return _value;
    }
}

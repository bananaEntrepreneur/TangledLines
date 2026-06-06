package model.listeners;

public enum ListenerPriority {
    LOW(0),
    MEDIUM(50),
    HIGH(100);

    private final int _value;

    ListenerPriority(int value) {
        _value = value;
    }

    public int getValue() {
        return _value;
    }
}

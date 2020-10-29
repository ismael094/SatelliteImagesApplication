package gui;

import model.events.EventType;

import java.util.EventObject;

public class ExecutedEvent extends EventObject {
    private EventType type;
    private Object value;
    public ExecutedEvent(Object source, EventType type, Object value) {
        super(source);
        this.type = type;
        this.value = value;
    }

    public EventType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}


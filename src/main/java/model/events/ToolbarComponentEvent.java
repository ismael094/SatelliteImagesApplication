package model.events;

import java.util.EventObject;

public class ToolbarComponentEvent<T>  extends EventObject {
    private EventType.ComponentEventType event;
    private T value;
    public ToolbarComponentEvent(Object source, EventType.ComponentEventType event, T value) {
        super(source);
        this.event = event;
        this.value = value;
    }
    public EventType.ComponentEventType getToolbarEvent() { return event; }

    public T getValue() {
        return value;
    }

}
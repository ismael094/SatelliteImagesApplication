package model.events;

import java.util.EventObject;

public class ToolbarComponentEvent  extends EventObject {
    private EventType.ComponentEventType event;
    public ToolbarComponentEvent(Object source, EventType.ComponentEventType event) {
        super(source);
        this.event = event;
    }
    public EventType.ComponentEventType getToolbarEvent() { return event; }

}
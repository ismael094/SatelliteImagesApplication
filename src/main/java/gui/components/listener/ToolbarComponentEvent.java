package gui.components.listener;

import java.util.EventObject;

public class ToolbarComponentEvent  extends EventObject {
    private ComponentEventType event;
    public ToolbarComponentEvent(Object source, ComponentEventType event) {
        super(source);
        this.event = event;
    }
    public ComponentEventType getToolbarEvent() { return event; }

}
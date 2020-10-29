package gui.components.listener;

import model.events.EventType;

import java.util.EventObject;

public class ComponentEvent extends EventObject {
    private Object value;
    public ComponentEvent(Object source, Object value) {
        super(source);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}

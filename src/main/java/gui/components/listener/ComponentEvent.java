package gui.components.listener;


import java.util.EventObject;

/**
 * Component event
 */
public class ComponentEvent extends EventObject {
    private Object value;
    public ComponentEvent(Object source, Object value) {
        super(source);
        this.value = value;
    }

    /**
     * Get change value
     * @return Change value
     */
    public Object getValue() {
        return value;
    }
}

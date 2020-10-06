package model.list;

import java.util.EventObject;

public class ProductListEvent extends EventObject {
    private String actionCommand;
    public ProductListEvent(Object source, String actionCommand) {
        super(source);
        this.actionCommand = actionCommand;
    }
    public String getActionCommand() { return actionCommand; }

}

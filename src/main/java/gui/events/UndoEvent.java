package gui.events;

import controller.MainController;
import controller.interfaces.ModifiableTabItem;
import controller.interfaces.ProductListTabItem;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;

public class UndoEvent extends Event {

    public UndoEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabComponent().getActive();
        if (mainController.getTabComponent().getControllerOf(active) instanceof ModifiableTabItem) {
            ModifiableTabItem controllerOf = (ModifiableTabItem) mainController.getTabComponent().getControllerOf(active);
            controllerOf.undo();
        }
    }
}

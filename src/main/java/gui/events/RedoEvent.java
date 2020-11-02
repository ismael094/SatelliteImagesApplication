package gui.events;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;

public class RedoEvent extends Event {

    public RedoEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabComponent().getActive();
        if (mainController.getTabComponent().getControllerOf(active) instanceof ProductListTabItem) {
            ProductListTabItem controllerOf = (ProductListTabItem) mainController.getTabComponent().getControllerOf(active);
            controllerOf.redo();
        }
    }
}

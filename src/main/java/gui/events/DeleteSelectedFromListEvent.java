package gui.events;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import model.list.ProductListDTO;
import utils.FileUtils;

import java.util.List;

public class DeleteSelectedFromListEvent extends Event {
    public DeleteSelectedFromListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabController().getActive();
        TabItem controllerOf = mainController.getTabController().getControllerOf(active);
        ProductListDTO list;
        if (controllerOf instanceof ListInformationController) {
            ListInformationController listController = (ListInformationController)controllerOf;
            list = listController.getProductList();
            list.getProducts().removeAll(listController.getSelectedProducts());
            //mainController.getToolBarComponent().fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_UPDATED,"List succesfully edited " + list.getName()));
        }

    }
}

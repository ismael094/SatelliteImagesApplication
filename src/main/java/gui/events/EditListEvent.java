package gui.events;

import controller.MainController;
import controller.interfaces.TabItem;
import controller.list.ListCreateAndEditController;
import controller.list.ListInformationController;
import gui.ExecutedEvent;
import gui.dialog.ListDialog;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.list.ProductListDTO;

import java.util.List;

public class EditListEvent extends Event {

    public EditListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabComponent().getActive();
        TabItem controllerOf = mainController.getTabComponent().getControllerOf(active);
        ProductListDTO list;
        if (controllerOf instanceof ListInformationController) {
            ListInformationController listController = (ListInformationController)controllerOf;
            list = listController.getProductList();

        } else {
            List<ProductListDTO> productList = showAndGetList(SelectionMode.SINGLE,"Select list to edit");
            if (productList.size() == 0)
                return;
            else
                list = productList.get(0);
        }
        ListDialog edit_list = new ListDialog("Edit list");
        ListCreateAndEditController load = edit_list.load();
        load.setProductList(list);
        edit_list.showAndWait();
        load.getProductList();
        if (load.isListWasCreated()) {
            mainController.fireEvent(new ExecutedEvent(this, EventType.LIST,"List successfully edited " + list.getName()));
        }

        //mainController.getToolBarComponent().fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_UPDATED,"List succesfully edited " + list.getName()));
    }
}

package gui.events;

import controller.MainController;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import gui.ExecutedEvent;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.list.ProductListDTO;
import services.database.ProductListDBDAO;

public class DeleteSelectedFromListEvent extends Event {
    public DeleteSelectedFromListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabComponent().getActive();
        TabItem controllerOf = mainController.getTabComponent().getControllerOf(active);
        ProductListDTO list;
        //If the active tab is a ListInformationController
        if (controllerOf instanceof ListInformationController) {
            ListInformationController listController = (ListInformationController)controllerOf;
            list = listController.getProductList();
            //Remove selected products from list
            if (!listController.getSelectedProducts().isEmpty()) {
                list.remove(listController.getSelectedProducts());
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        ProductListDBDAO.getInstance().save(list);
                        return null;
                    }
                };
                new Thread(task).start();
                mainController.fireEvent(new ExecutedEvent(this, EventType.LIST,"Selected products deleted from list " + list.getName()));

            }
            //mainController.getToolBarComponent().fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_UPDATED,"List succesfully edited " + list.getName()));
        }

    }
}

package gui.events;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import gui.ExecutedEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.products.ProductDTO;

public class DownloadSelectedProductEvent extends Event {

    public DownloadSelectedProductEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabComponent().getActive();
        TabItem controllerOf = mainController.getTabComponent().getControllerOf(active);
        if (controllerOf instanceof ProductListTabItem) {
            ObservableList<ProductDTO> selectedProducts = ((ProductListTabItem) controllerOf).getSelectedProducts();
            selectedProducts.forEach(p->{
                mainController.getDownloader().download(p);
            });
        }

        mainController.fireEvent(new ExecutedEvent(this, EventType.DOWNLOAD,"Products deleted from list"));
        //mainController.getToolBarComponent().fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_UPDATED,"Products deleted from list"));
    }
}

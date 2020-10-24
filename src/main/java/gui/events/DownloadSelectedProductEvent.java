package gui.events;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import model.list.ProductListDTO;
import model.products.ProductDTO;

public class DownloadSelectedProductEvent extends Event {

    public DownloadSelectedProductEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabController().getActive();
        TabItem controllerOf = mainController.getTabController().getControllerOf(active);
        if (controllerOf instanceof ProductListTabItem) {
            ObservableList<ProductDTO> selectedProducts = ((ProductListTabItem) controllerOf).getSelectedProducts();
            selectedProducts.forEach(p->{
                mainController.getDownload().download(p);
            });
        }
        //mainController.getToolBarComponent().fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_UPDATED,"Products deleted from list"));
    }
}

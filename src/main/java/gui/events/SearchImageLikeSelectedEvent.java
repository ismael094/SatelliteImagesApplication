package gui.events;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import gui.ExecutedEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import utils.gui.Observer;

public class SearchImageLikeSelectedEvent extends Event {

    public SearchImageLikeSelectedEvent(MainController controller) {
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
            mainController.fireEvent(new ExecutedEvent(this, EventType.LIST,"Products deleted from list"));
        }
    }
}

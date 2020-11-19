package gui.events;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import services.entities.ProductList;

public class SelectProductInProductListEvent extends Event {
    private ProductListDTO list;
    private ObservableList<ProductDTO> products;

    public SelectProductInProductListEvent(MainController controller, ProductListDTO list, ObservableList<ProductDTO> products) {
        super(controller);
        this.list = list;
        this.products = products;
    }

    @Override
    public void handle(ActionEvent event) {
        TabItem controller = mainController.getTabComponent().getControllerOf(list.getId().toString());
        if (controller != null) {
            ((ProductListTabItem) controller).setSelectedProducts(products);
        }
    }
}

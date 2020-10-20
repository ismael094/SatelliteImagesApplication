package gui.events;

import controller.MainController;
import controller.interfaces.TabItem;
import controller.search.SearchController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import utils.DownloadConfiguration;

import java.util.List;

public class AddSelectedToListEvent extends Event {

    public AddSelectedToListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        ObservableList<ProductDTO> openSearcher = getSelectedProducts();
        if (openSearcher == null || openSearcher.size() == 0) {
            event.consume();
            return;
        }
        List<ProductListDTO> productListDTO = getProductLists();
        if (productListDTO.size()>0){
            productListDTO.forEach(pL->pL.addProduct(openSearcher));
            mainController.getToolBarComponent().fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_UPDATED, "Products added to list"));
            if (DownloadConfiguration.getAutodownload())
                openSearcher.forEach(p->{
                    mainController.getDownload().download(p);
                });
        }
    }

    private ObservableList<ProductDTO> getSelectedProducts() {
        Tab tab = mainController.getTabController().getActive();
        TabItem controller = mainController.getTabController().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController<ProductDTO>) controller).getSelectedProducts();
        return null;
    }
}
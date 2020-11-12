package gui.events;

import controller.MainController;
import controller.interfaces.TabItem;
import controller.search.SearchController;
import gui.ExecutedEvent;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import services.database.ProductListDBDAO;
import utils.DownloadConfiguration;

import java.util.List;

public class AddAllToListEvent extends Event{
    public AddAllToListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        ObservableList<ProductDTO> openSearcher = getAllProducts();
        if (openSearcher == null || openSearcher.size() == 0) {
            event.consume();
            return;
        }

        List<ProductListDTO> productListDTO = getProductLists();
        if (!productListDTO.isEmpty()){
            productListDTO.forEach(pL-> {
                pL.addProduct(openSearcher);
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        ProductListDBDAO.getInstance().save(pL);
                        return null;
                    }
                };
                new Thread(task).start();
            });
            mainController.fireEvent(new ExecutedEvent(this, EventType.LIST,"Products added to lists"));

            //menuComponent.fireEvent(new ComponentEvent(this, "Products added to lists"));
            if (DownloadConfiguration.getAutodownload())
                openSearcher.forEach(p-> mainController.getDownloader().download(p));
        }
    }

    private ObservableList<ProductDTO> getAllProducts() {
        Tab tab = mainController.getTabComponent().getSelectionModel().getSelectedItem();
        TabItem controller = mainController.getTabComponent().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController) controller).getProducts();
        return null;
    }
}

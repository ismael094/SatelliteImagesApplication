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

public class AddSelectedToListEvent extends Event {

    public AddSelectedToListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        //Get the products selected in the SearchTabItem
        ObservableList<ProductDTO> openSearcher = getSelectedProducts();
        if (openSearcher == null || openSearcher.size() == 0) {
            event.consume();
            return;
        }

        //Select product list and add products
        List<ProductListDTO> productListDTO = getProductLists();
        if (productListDTO.size()>0){
            productListDTO.forEach(pL->{
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

            //If autodownload is enabled, download product
            if (DownloadConfiguration.getAutodownload())
                openSearcher.forEach(p->{
                    mainController.getDownloader().download(p);
                });
        }
    }

    private ObservableList<ProductDTO> getSelectedProducts() {
        Tab tab = mainController.getTabComponent().getActive();
        TabItem controller = mainController.getTabComponent().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController<ProductDTO>) controller).getSelectedProducts();
        return null;
    }
}

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
import utils.AlertFactory;
import utils.SatelliteHelper;

import java.util.ArrayList;
import java.util.List;

public class AddReferenceImageEvent extends Event {

    public AddReferenceImageEvent(MainController controller) {
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

        //Get the product list
        ProductListDTO productListDTO = getSingleProductList();
        List<ProductDTO> validProducts = new ArrayList<>();
        if (productListDTO != null) {

            openSearcher.forEach(p->{
                //If the product is an optical image and intersect an area of work, add reference image
                if (!SatelliteHelper.isRadar(p.getPlatformName()) && productListDTO.areasOfWorkOfProduct(p.getFootprint()).size() > 0) {
                    validProducts.add(p);
                    mainController.fireEvent(new ExecutedEvent(this, EventType.LIST,"Reference image added"));
                    AlertFactory.showSuccessDialog("Ground Truth",
                            "Ground truth product successfully added","Product " + p.getTitle()+
                            " successfully added as ground truth in list named " + productListDTO.getName());
                } else {
                    AlertFactory.showErrorDialog("Error","Error",
                            "Product not valid as a ground truth. Must be a Sentinel-2 image and contain an area of work");
                }
            });
            productListDTO.addReferenceProduct(validProducts);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ProductListDBDAO.getInstance().save(productListDTO);
                    return null;
                }
            };
            new Thread(task).start();
        } else {
            AlertFactory.showErrorDialog("Error","Error","Product List not selected");
        }
    }


    private ObservableList<ProductDTO> getSelectedProducts() {
        Tab tab = mainController.getTabComponent().getSelectionModel().getSelectedItem();
        TabItem controller = mainController.getTabComponent().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController) controller).getSelectedProducts();
        return null;
    }
}

package gui.events;

import controller.MainController;
import controller.interfaces.TabItem;
import controller.search.SearchController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import utils.AlertFactory;

import java.util.ArrayList;
import java.util.List;

public class AddReferenceImageEvent extends Event {

    public AddReferenceImageEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        ObservableList<ProductDTO> openSearcher = getSelectedProducts();
        if (openSearcher == null || openSearcher.size() == 0) {
            event.consume();
            return;
        }
        ProductListDTO productListDTO = getSingleProductList();
        List<ProductDTO> validProducts = new ArrayList<>();
        if (productListDTO != null){
            openSearcher.forEach(p->{
                if (p.getPlatformName().equals("Sentinel-2") && productListDTO.areasOfWorkOfProduct(p.getFootprint()).size() > 0) {
                    validProducts.add(p);
                    AlertFactory.showSuccessDialog("Ground Truth","Ground truth product succesfully added","Product " + p.getTitle()+
                            " successfully added as ground truth in list named " + productListDTO.getName());
                } else {
                    AlertFactory.showErrorDialog("Error","Error","Product not valid as a ground truth. Must be a Sentinel-2 image and contain an area of work");
                }
            });
            productListDTO.addGroundTruthProduct(validProducts);
        } else {
            AlertFactory.showErrorDialog("Error","Error","ERROR");
        }
    }


    private ObservableList<ProductDTO> getSelectedProducts() {
        Tab tab = mainController.getTabController().getSelectionModel().getSelectedItem();
        TabItem controller = mainController.getTabController().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController) controller).getSelectedProducts();
        return null;
    }
}
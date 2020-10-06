package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.search.SearchController;
import gui.components.ToolBarComponent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Duration;
import model.list.ProductListDTO;
import model.products.ProductDTO;

import java.util.List;

public class AddAllToListToolbarButton extends ToolbarButton{

    public AddAllToListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("selectAll");
    }


    @Override
    public void init() {
        setOnAction(this);
        Tooltip tooltip = new Tooltip("Add all products to list");
        tooltip.setShowDelay(new Duration(0.1));
        tooltip.setHideDelay(new Duration(0.5));
        setTooltip(tooltip);
    }

    @Override
    public void handle(ActionEvent event) {


        ObservableList<ProductDTO> openSearcher = getAllProducts();
        if (openSearcher == null || openSearcher.size() == 0) {
            event.consume();
            return;
        }

        List<ProductListDTO> productListDTO = getProductList();
        if (productListDTO.size()>0){
            productListDTO.forEach(pL->pL.addProduct(openSearcher));
            toolBar.getMainController().getListTreeViewController().reload();
        }
    }



    private ObservableList<ProductDTO> getAllProducts() {
        Tab tab = toolBar.getMainController().getTabController().getSelectionModel().getSelectedItem();
        TabItem controller = toolBar.getMainController().getTabController().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController) controller).getProducts();
        return null;
    }
}

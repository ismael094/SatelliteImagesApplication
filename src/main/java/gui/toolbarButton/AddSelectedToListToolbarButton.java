package gui.toolbarButton;

import controller.TabItem;
import controller.search.SearchController;
import gui.components.ToolBarComponent;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.ProductList;
import model.products.Product;

import java.util.List;
import java.util.Objects;

public class AddSelectedToListToolbarButton extends ToolbarButton {

    public AddSelectedToListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("addToList");
    }


    @Override
    public void init() {
        setOnAction(this);
        Tooltip tooltip = new Tooltip("Add selected products to list");
        tooltip.setShowDelay(new Duration(0.1));
        tooltip.setHideDelay(new Duration(0.5));
        setTooltip(tooltip);
        disableProperty().bind(toolBar.getMainController().getTabController().getIsSearchControllerOpenProperty().not());
    }

    @Override
    public void handle(ActionEvent event) {
        ObservableList<Product> openSearcher = getSelectedProducts();
        if (openSearcher == null || openSearcher.size() == 0) {
            event.consume();
            return;
        }

        List<ProductList> productList = getProductList();
        if (productList.size()>0){
            productList.forEach(pL->openSearcher.forEach(pL::addProduct));
            toolBar.getMainController().getListTreeViewController().reloadTree();
        }
    }



    private ObservableList<Product> getSelectedProducts() {
        Tab tab = toolBar.getMainController().getTabController().getSelectionModel().getSelectedItem();
        TabItem controller = toolBar.getMainController().getTabController().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController) controller).getSelectedProducts();
        return null;
    }
}

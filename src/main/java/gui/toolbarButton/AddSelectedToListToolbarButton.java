package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.search.SearchController;
import gui.components.ToolBarComponent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;
import model.products.ProductDTO;

import java.util.List;

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
        ObservableList<ProductDTO> openSearcher = getSelectedProducts();
        if (openSearcher == null || openSearcher.size() == 0) {
            event.consume();
            return;
        }
        List<ProductListDTO> productListDTO = getProductList();
        if (productListDTO.size()>0){
            productListDTO.forEach(pL->pL.addProduct(openSearcher));
            toolBar.fireEvent(new ToolbarComponentEvent(this, EventType.ComponentEventType.LIST_UPDATED));
        }
    }



    private ObservableList<ProductDTO> getSelectedProducts() {
        Tab tab = toolBar.getMainController().getTabController().getSelectionModel().getSelectedItem();
        TabItem controller = toolBar.getMainController().getTabController().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController) controller).getSelectedProducts();
        return null;
    }
}

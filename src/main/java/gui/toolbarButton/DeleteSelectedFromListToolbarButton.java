package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.interfaces.ProductTabItem;
import gui.components.ToolBarComponent;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.list.ProductListDTO;

public class DeleteSelectedFromListToolbarButton extends ToolbarButton {

    public DeleteSelectedFromListToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("deleteSelected");
    }

    @Override
    public void init() {
        setOnAction(this);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = toolBar.getMainController().getTabController().getActive();
        TabItem controllerOf = toolBar.getMainController().getTabController().getControllerOf(active);
        if (controllerOf instanceof ProductTabItem) {
            ProductListDTO productListDTO = ((ProductTabItem) controllerOf).getProductList();
            productListDTO.remove(((ProductTabItem)controllerOf).getSelectedProducts());
        }
    }
}

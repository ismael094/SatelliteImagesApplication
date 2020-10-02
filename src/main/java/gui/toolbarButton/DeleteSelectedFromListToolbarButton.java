package gui.toolbarButton;

import controller.TabItem;
import controller.list.ListItem;
import gui.components.ToolBarComponent;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.ProductList;

import java.util.List;

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
        if (controllerOf instanceof ListItem) {
            ProductList productList = ((ListItem) controllerOf).getProductList();
            productList.remove(((ListItem)controllerOf).getSelectedProducts());
        }
    }
}

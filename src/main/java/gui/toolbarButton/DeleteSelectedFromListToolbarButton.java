package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.interfaces.ProductListTabItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
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
        setIcon(MaterialDesignIcon.DELETE_VARIANT,"1.5em");
        setTooltip("Delete all products selected");
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = toolBar.getMainController().getTabController().getActive();
        TabItem controllerOf = toolBar.getMainController().getTabController().getControllerOf(active);
        if (controllerOf instanceof ProductListTabItem) {
            ProductListDTO productListDTO = ((ProductListTabItem) controllerOf).getProductList();
            productListDTO.remove(((ProductListTabItem)controllerOf).getSelectedProducts());
        }
        toolBar.fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_UPDATED,"Products deleted from list"));
    }
}

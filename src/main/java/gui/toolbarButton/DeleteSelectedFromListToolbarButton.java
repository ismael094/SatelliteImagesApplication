package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.interfaces.ProductTabItem;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
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
        if (controllerOf instanceof ProductTabItem) {
            ProductListDTO productListDTO = ((ProductTabItem) controllerOf).getProductList();
            productListDTO.remove(((ProductTabItem)controllerOf).getSelectedProducts());
        }
        toolBar.fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_UPDATED,"Products deleted from list"));
    }
}

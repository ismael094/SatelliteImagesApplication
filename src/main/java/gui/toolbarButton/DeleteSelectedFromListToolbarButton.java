package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.interfaces.ProductListTabItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.DeleteSelectedFromListEvent;
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
        setOnAction(new DeleteSelectedFromListEvent(toolBar.getMainController()));
        setIcon(MaterialDesignIcon.DELETE_VARIANT,"1.5em");
        setTooltip("Delete all products selected");
    }
}

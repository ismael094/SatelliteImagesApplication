package gui.toolbarButton;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.DeleteSelectedFromListEvent;

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

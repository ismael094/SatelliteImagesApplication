package gui.toolbarButton;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.DeleteListEvent;

public class DeleteListToolbarButton extends ToolbarButton {

    public DeleteListToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("deleteList");
    }

    @Override
    public void init() {
        setOnAction(new DeleteListEvent(toolBar.getMainController()));
        setIcon(MaterialDesignIcon.FOLDER_REMOVE,"1.5em");
        setTooltip("Delete list");
    }

    @Override
    public void update(Object args) {

    }
}

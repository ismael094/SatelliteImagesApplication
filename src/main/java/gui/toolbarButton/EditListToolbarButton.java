package gui.toolbarButton;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.EditListEvent;

public class EditListToolbarButton extends ToolbarButton {

    public EditListToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("editList");
    }

    @Override
    public void init() {
        setOnAction(new EditListEvent(toolBar.getMainController()));
        setIcon(MaterialDesignIcon.PENCIL_BOX,"1.5em");
        setTooltip("Edit selected list");
    }

    @Override
    public void update(Object args) {

    }


}

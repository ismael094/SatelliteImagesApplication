package gui.toolbarButton;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.CreateListEvent;

public class CreateListToolbarButton extends ToolbarButton {

    public CreateListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("createList");
    }

    @Override
    public void init() {
        setOnAction(new CreateListEvent(toolBar.getMainController()));

        setIcon(MaterialDesignIcon.FOLDER_PLUS,"1.5em");
        setTooltip("Create new list");
    }

    @Override
    public void update(Object args) {

    }
}

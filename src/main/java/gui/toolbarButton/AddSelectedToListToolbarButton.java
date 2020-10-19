package gui.toolbarButton;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.AddSelectedToListEvent;

public class AddSelectedToListToolbarButton extends ToolbarButton {

    public AddSelectedToListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("addToList");
    }


    @Override
    public void init() {
        setOnAction(new AddSelectedToListEvent(toolBar.getMainController()));
        setIcon(MaterialDesignIcon.IMAGE_AREA_CLOSE,"1.5em");
        setTooltip("Add selected products to list");
        disableProperty().bind(toolBar.getMainController().getTabController().getIsSearchControllerOpenProperty().not());
    }
}

package gui.toolbarButton;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.AddReferenceImageEvent;

public class AddReferenceImageToListToolbarButton extends ToolbarButton {

    public AddReferenceImageToListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("addToListGround");
    }


    @Override
    public void init() {
        setOnAction(new AddReferenceImageEvent(toolBar.getMainController()));
        setIcon(MaterialDesignIcon.IMAGE_AREA_CLOSE,"1.5em");
        setTooltip("Add selected products as ground truth");
        disableProperty().bind(toolBar.getMainController().getTabController().getIsSearchControllerOpenProperty().not());
    }


}

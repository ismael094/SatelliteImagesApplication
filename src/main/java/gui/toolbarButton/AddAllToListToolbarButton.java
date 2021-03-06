package gui.toolbarButton;

import controller.search.SearchController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.AddAllToListEvent;
import javafx.application.Platform;

public class AddAllToListToolbarButton extends ToolbarButton {

    public AddAllToListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("selectAll");
    }


    @Override
    public void init() {
        setOnAction(new AddAllToListEvent(toolBar.getMainController()));
        setIcon(MaterialDesignIcon.IMAGE_MULTIPLE,"1.5em");
        setTooltip("Add all products to list");
    }

    @Override
    public void update(Object args) {
        Platform.runLater(()-> setDisable(!(args instanceof SearchController)));
    }
}

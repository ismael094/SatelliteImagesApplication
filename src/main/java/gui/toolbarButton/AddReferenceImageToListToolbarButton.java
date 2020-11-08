package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.search.SearchController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.components.TabPaneComponent;
import gui.events.AddReferenceImageEvent;
import javafx.application.Platform;
import utils.gui.Observer;

public class AddReferenceImageToListToolbarButton extends ToolbarButton implements Observer {

    public AddReferenceImageToListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("addToListGround");
    }


    @Override
    public void init() {
        setOnAction(new AddReferenceImageEvent(toolBar.getMainController()));
        setIcon(FontAwesomeIcon.FILE_IMAGE_ALT,"1.5em");
        setTooltip("Add selected products as ground truth");
        //toolBar.getMainController().getTabComponent().addObserver(this);
        //disableProperty().bind(toolBar.getMainController().getTabComponent().getIsSearchControllerOpenProperty().not());
    }

    @Override
    public void update(Object args) {
        Platform.runLater(()-> setDisable(!(args instanceof SearchController)));
    }
}

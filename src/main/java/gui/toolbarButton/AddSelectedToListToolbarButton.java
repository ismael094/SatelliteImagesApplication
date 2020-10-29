package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.search.SearchController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.components.tabcomponent.TabPaneComponent;
import gui.events.AddSelectedToListEvent;
import javafx.application.Platform;
import utils.gui.Observer;

public class AddSelectedToListToolbarButton extends ToolbarButton implements Observer {

    public AddSelectedToListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("addToList");
    }


    @Override
    public void init() {
        setOnAction(new AddSelectedToListEvent(toolBar.getMainController()));
        setIcon(MaterialDesignIcon.IMAGE_AREA_CLOSE,"1.5em");
        setTooltip("Add selected products to list");
        toolBar.getMainController().getTabComponent().addObserver(this);
        //disableProperty().bind(toolBar.getMainController().getTabComponent().getIsSearchControllerOpenProperty().not());
    }

    @Override
    public void update() {
        TabPaneComponent tabComponent = toolBar.getMainController().getTabComponent();
        Platform.runLater(()->{
            TabItem controllerOf = tabComponent.getControllerOf(tabComponent.getActive());
            this.setDisable(!(controllerOf instanceof SearchController));
        });
    }
}

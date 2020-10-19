package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.search.SearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.AddSelectedToListEvent;
import gui.events.DeleteListEvent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import services.download.DownloadItem;
import utils.DownloadConfiguration;

import java.util.List;

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

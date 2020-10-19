package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.search.SearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.AddReferenceImageEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import utils.AlertFactory;

import java.util.ArrayList;
import java.util.List;

public class AddGroundTruthToListToolbarButton extends ToolbarButton {

    public AddGroundTruthToListToolbarButton(ToolBarComponent toolBarComponent) {
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

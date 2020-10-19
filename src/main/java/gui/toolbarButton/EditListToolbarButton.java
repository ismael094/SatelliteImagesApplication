package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.list.ListCreateAndEditController;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.EditListEvent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import gui.dialog.ListDialog;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;

import java.util.List;

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
}

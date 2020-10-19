package gui.toolbarButton;

import controller.list.ListCreateAndEditController;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.AddReferenceImageEvent;
import gui.events.CreateListEvent;
import gui.events.DeleteListEvent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import gui.dialog.ListDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;
import utils.AlertFactory;

import static java.lang.System.currentTimeMillis;

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
}

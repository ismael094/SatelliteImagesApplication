package gui.toolbarButton;

import controller.interfaces.ProductListTabItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.components.ToolBarComponent;
import gui.events.ProcessListEvent;
import javafx.application.Platform;

public class ProcessListToolbarButton extends ToolbarButton {
    public ProcessListToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("processList");
    }

    @Override
    public void init() {
        setOnAction(new ProcessListEvent(toolBar.getMainController()));
        setIcon(FontAwesomeIcon.ROCKET,"1.5em");
        setTooltip("Process current list");
    }

    @Override
    public void update(Object args) {
        Platform.runLater(()-> setDisable(!(args instanceof ProductListTabItem)));
    }
}

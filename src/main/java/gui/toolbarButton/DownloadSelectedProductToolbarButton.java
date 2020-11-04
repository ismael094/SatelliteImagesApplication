package gui.toolbarButton;

import controller.interfaces.ProductListTabItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.DownloadSelectedProductEvent;
import javafx.application.Platform;

public class DownloadSelectedProductToolbarButton  extends ToolbarButton{

    public DownloadSelectedProductToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("downloadSingle");
    }

    @Override
    public void init() {
        setOnAction(new DownloadSelectedProductEvent(toolBar.getMainController()));
        setIcon(MaterialDesignIcon.DOWNLOAD,"1.5em");
        setTooltip("Download selected products in the current list");
    }

    @Override
    public void update(Object args) {
        Platform.runLater(()-> setDisable(!(args instanceof ProductListTabItem)));
    }
}
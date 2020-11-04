package gui.toolbarButton;

import controller.interfaces.ProductListTabItem;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.DownloadProductListEvent;
import javafx.application.Platform;

public class DownloadProductListToolbarButton extends ToolbarButton{

    public DownloadProductListToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("downloadAll");
    }

    @Override
    public void init() {

        setOnAction(new DownloadProductListEvent(toolBar.getMainController()));
        setIcon(MaterialDesignIcon.FOLDER_DOWNLOAD,"1.5em");
        setTooltip("Download all product in the current list");
    }

    @Override
    public void update(Object args) {
        Platform.runLater(()-> setDisable(!(args instanceof ProductListTabItem)));
    }
}

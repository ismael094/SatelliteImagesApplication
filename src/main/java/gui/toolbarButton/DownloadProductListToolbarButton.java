package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.DownloadProductListEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import model.list.ProductListDTO;
import services.download.DownloadItem;
import utils.FileUtils;

import java.util.List;

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
}

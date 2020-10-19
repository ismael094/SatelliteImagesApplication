package gui.toolbarButton;

import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import gui.events.DownloadSelectedProductEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.products.ProductDTO;
import services.download.CopernicusDownloader;
import services.download.DownloadItem;
import services.download.Downloader;

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
}
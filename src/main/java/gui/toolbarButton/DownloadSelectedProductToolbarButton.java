package gui.toolbarButton;

import controller.interfaces.ProductTabItem;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.products.ProductDTO;
import services.download.DownloadItem;
import services.download.DownloadManager;

public class DownloadSelectedProductToolbarButton  extends ToolbarButton{

    public DownloadSelectedProductToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("downloadSingle");
    }

    @Override
    public void init() {
        setOnAction(this);
        setIcon(MaterialDesignIcon.DOWNLOAD,"1.5em");
        setTooltip("Download selected products in the current list");
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = toolBar.getMainController().getTabController().getActive();
        TabItem controllerOf = toolBar.getMainController().getTabController().getControllerOf(active);

        if (controllerOf instanceof ProductTabItem) {
            ListInformationController listController = (ListInformationController)controllerOf;
            ObservableList<ProductDTO> selectedProducts = listController.getSelectedProducts();
            DownloadManager download = toolBar.getMainController().getDownload();
            selectedProducts.forEach(p->{
                download.add(new DownloadItem(p));
            });
        }
    }
}
package gui.toolbarButton;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import gui.components.ToolBarComponent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import services.download.DownloadItem;
import services.download.DownloadManager;
import utils.FileUtils;

import java.util.List;

public class DownloadSelectedProductToolbarButton  extends ToolbarButton{

    public DownloadSelectedProductToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("downloadSingle");
    }

    @Override
    public void init() {
        setOnAction(this);
        Tooltip tooltip = new Tooltip("Download selected products in the current list");
        tooltip.setShowDelay(new Duration(0.1));
        tooltip.setHideDelay(new Duration(0.5));
        setTooltip(tooltip);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = toolBar.getMainController().getTabController().getActive();
        TabItem controllerOf = toolBar.getMainController().getTabController().getControllerOf(active);

        if (controllerOf instanceof ListInformationController) {
            ListInformationController listController = (ListInformationController)controllerOf;
            ObservableList<ProductDTO> selectedProducts = listController.getSelectedProducts();
            DownloadManager download = toolBar.getMainController().getDownload();
            selectedProducts.forEach(p->{
                download.add(new DownloadItem(p));
            });
        }
    }
}
package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;
import services.download.DownloadItem;
import services.download.DownloadManager;
import utils.FileUtils;

import java.util.List;

public class DownloadProductListToolbarButton extends ToolbarButton{

    public DownloadProductListToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("downloadAll");
    }

    @Override
    public void init() {

        setOnAction(this);
        setIcon(MaterialDesignIcon.FOLDER_DOWNLOAD,"1.5em");
        setTooltip("Download all product in the current list");
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = toolBar.getMainController().getTabController().getActive();
        TabItem controllerOf = toolBar.getMainController().getTabController().getControllerOf(active);
        ProductListDTO list;
        if (controllerOf instanceof ListInformationController) {
            ListInformationController listController = (ListInformationController)controllerOf;
            list = listController.getProductList();

        } else {
            List<ProductListDTO> productList = showAndGetList(SelectionMode.SINGLE,"Select list to edit");
            if (productList.size() == 0)
                return;
            else
                list = productList.get(0);
        }

        FileUtils.saveObjectToJson(list);

        list.getProducts().forEach(p-> toolBar.getMainController().getDownload().add(new DownloadItem(p)));

        list.getGroundTruthProducts().forEach(p-> toolBar.getMainController().getDownload().add(new DownloadItem(p)));

    }
}

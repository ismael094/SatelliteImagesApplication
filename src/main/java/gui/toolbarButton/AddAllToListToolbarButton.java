package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.search.SearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Duration;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import services.download.DownloadItem;
import utils.DownloadConfiguration;

import java.util.List;

public class AddAllToListToolbarButton extends ToolbarButton{

    public AddAllToListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("selectAll");
    }


    @Override
    public void init() {
        setOnAction(this);
        setIcon(MaterialDesignIcon.IMAGE_MULTIPLE,"1.5em");
        setTooltip("Add all products to list");
    }

    @Override
    public void handle(ActionEvent event) {
        ObservableList<ProductDTO> openSearcher = getAllProducts();
        if (openSearcher == null || openSearcher.size() == 0) {
            event.consume();
            return;
        }

        List<ProductListDTO> productListDTO = getProductLists();
        if (!productListDTO.isEmpty()){
            productListDTO.forEach(pL->{
                pL.addProduct(openSearcher);
            });


            toolBar.fireEvent(new ToolbarComponentEvent<String>(this, EventType.ComponentEventType.LIST_UPDATED, "Products added to lists"));
            if (DownloadConfiguration.getAutodownload())
                openSearcher.forEach(p-> toolBar.getMainController().getDownload().download(p));
        }
    }



    private ObservableList<ProductDTO> getAllProducts() {
        Tab tab = toolBar.getMainController().getTabController().getSelectionModel().getSelectedItem();
        TabItem controller = toolBar.getMainController().getTabController().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController) controller).getProducts();
        return null;
    }
}

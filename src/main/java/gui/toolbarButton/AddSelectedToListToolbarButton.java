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
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import services.download.DownloadItem;
import utils.DownloadConfiguration;

import java.util.List;

public class AddSelectedToListToolbarButton extends ToolbarButton {

    public AddSelectedToListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("addToList");
    }


    @Override
    public void init() {
        setOnAction(this);
        setIcon(MaterialDesignIcon.IMAGE_AREA_CLOSE,"1.5em");
        setTooltip("Add selected products to list");
        disableProperty().bind(toolBar.getMainController().getTabController().getIsSearchControllerOpenProperty().not());
    }

    @Override
    public void handle(ActionEvent event) {
        ObservableList<ProductDTO> openSearcher = getSelectedProducts();
        if (openSearcher == null || openSearcher.size() == 0) {
            event.consume();
            return;
        }
        List<ProductListDTO> productListDTO = getProductLists();
        if (productListDTO.size()>0){
            productListDTO.forEach(pL->pL.addProduct(openSearcher));
            toolBar.fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_UPDATED, "Products added to list"));
            if (DownloadConfiguration.getAutodownload())
                openSearcher.forEach(p->{
                    toolBar.getMainController().getDownload().download(p);
                });
        }
    }



    private ObservableList<ProductDTO> getSelectedProducts() {
        Tab tab = toolBar.getMainController().getTabController().getSelectionModel().getSelectedItem();
        TabItem controller = toolBar.getMainController().getTabController().getControllerOf(tab);
        if (controller instanceof SearchController)
            return ((SearchController) controller).getSelectedProducts();
        return null;
    }
}

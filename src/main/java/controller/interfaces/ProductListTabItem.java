package controller.interfaces;

import javafx.collections.ObservableList;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import services.download.Downloader;

import java.util.prefs.Preferences;

/**
 * Class to handle productList controllers
 */
public abstract class ProductListTabItem implements TabItem, ModifiableTabItem {
    private final Downloader downloader;

    public ProductListTabItem(Downloader downloader) {
        this.downloader = downloader;

        downloader.addListener(l->{
            refreshProducts();
        });
        Preferences.userRoot().node("downloadPreferences").addPreferenceChangeListener(event -> refreshProducts());
    }

    /**
     * Get the productList assign to the controller
     * @return product list
     */
    public abstract ProductListDTO getProductList();
    public abstract ObservableList<ProductDTO> getSelectedProducts();
    public abstract void setSelectedProducts(ObservableList<ProductDTO> products);
    public abstract void refreshProducts();

    /**
     * Enumeration to identified operations to handle modificabletabitem
     */
    protected enum ListAction {
        ADD_PRODUCT("Add product"),
        DELETE_PRODUCT("Delete product"),
        ADD_AREA_OF_WORK("Add area of work"),
        DELETE_AREA_OF_WORK("Delete area of work"),
        ADD_REFERENCE_IMAGE("Add reference image"),
        DELETE_REFERENCE_IMAGE("Delete reference image");

        private final String name;

        ListAction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

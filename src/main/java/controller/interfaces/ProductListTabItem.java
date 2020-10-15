package controller.interfaces;

import javafx.collections.ObservableList;
import model.events.EventType;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import services.download.CopernicusDownloader;
import services.download.Downloader;

import java.util.prefs.Preferences;

public abstract class ProductListTabItem implements TabItem{
    private final Downloader downloader;

    public ProductListTabItem(Downloader downloader) {
        this.downloader = downloader;
        downloader.addListener(EventType.DownloadEventType.COMPLETED, event -> {
            refreshProducts();
            System.out.println("REFRESHING");
        });
        Preferences.userRoot().node("downloadPreferences").addPreferenceChangeListener(event -> refreshProducts());
    }

    public abstract ProductListDTO getProductList();
    public abstract ObservableList<ProductDTO> getSelectedProducts();
    public abstract void setSelectedProducts(ObservableList<ProductDTO> products);
    public abstract void refreshProducts();
}

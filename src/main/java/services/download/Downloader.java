package services.download;

import javafx.beans.property.DoubleProperty;
import model.list.ProductListDTO;
import model.listeners.ComponentChangeListener;
import model.products.ProductDTO;

public interface Downloader {
    /**
     * Add listener to download
     * @param listener new listener
     */
    void addListener(ComponentChangeListener listener);

    /**
     * Download new 
     * @param productList
     */
    void download(ProductListDTO productList);
    void download(ProductDTO productDTO);
    DoubleProperty timeLeftProperty();
    void cancel();
    void pause();
    void resume();
    void remove(ProductDTO productDTO);
    boolean isDownloading();
}

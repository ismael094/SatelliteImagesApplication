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
     * Download productList
     * @param productList productList to download
     */
    void download(ProductListDTO productList);

    /**
     * Download product
     * @param productDTO productDTO to download
     */
    void download(ProductDTO productDTO);

    /**
     * Time left to finish the download
     * @return double property
     */
    DoubleProperty timeLeftProperty();

    /**
     * Cancel the downloads
     */
    void cancel();

    /**
     * Pause the downloads
     */
    void pause();

    /**
     * Resume the downloads
     */
    void resume();

    /**
     * Cancel product download
     * @param productDTO product to cancel download
     */
    void remove(ProductDTO productDTO);

    /**
     * Verify is products are been downloading
     * @return true if downloading; false otherwise
     */
    boolean isDownloading();
}

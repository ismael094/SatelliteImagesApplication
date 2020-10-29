package services.download;

import javafx.beans.property.DoubleProperty;
import model.events.EventType;
import model.list.ProductListDTO;
import model.listeners.ComponentChangeListener;
import model.listeners.DownloadListener;
import model.products.ProductDTO;

public interface Downloader {
    void addListener(ComponentChangeListener listener);
    void download(ProductListDTO productList);
    void download(ProductDTO productDTO);
    DoubleProperty timeLeftProperty();
    void cancel();
    void pause();
    void resume();
    void remove(ProductDTO productDTO);
}

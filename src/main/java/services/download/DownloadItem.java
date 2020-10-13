package services.download;

import javafx.beans.property.ReadOnlyDoubleProperty;
import model.products.ProductDTO;


public class DownloadItem {
    private final ProductDTO productDTO;
    private String location;
    private ReadOnlyDoubleProperty progressProperty;

    public DownloadItem(ProductDTO productDTO) {
        this.productDTO = productDTO;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return productDTO.getTitle().substring(0,20);
    }

    public ProductDTO getProductDTO() {
        return productDTO;
    }

    public String getLocation() {
        return location;
    }

    public ReadOnlyDoubleProperty getProgressProperty() {
        return progressProperty;
    }

    public void setProgressProperty(ReadOnlyDoubleProperty progressProperty) {
        this.progressProperty = progressProperty;
    }
}

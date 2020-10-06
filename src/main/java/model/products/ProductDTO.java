package model.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import utils.OpenSearchProductDeserializer;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

@JsonDeserialize(using = OpenSearchProductDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {
    protected StringProperty id;
    protected ObjectProperty<Calendar> ingestionDate;
    protected StringProperty title;
    protected StringProperty footprint;
    protected StringProperty size;
    protected StringProperty productType;
    protected StringProperty platformName;
    protected StringProperty status;

    public ProductDTO(StringProperty id, StringProperty title, StringProperty platformName, StringProperty productType, StringProperty footprint, StringProperty size, StringProperty status, ObjectProperty<Calendar> ingestionDate) {
        this.id = id;
        this.ingestionDate = ingestionDate;
        this.title = title;
        this.footprint = footprint;
        this.size = size;
        this.productType = productType;
        this.platformName = platformName;
        this.status = status;
    }

    public void setIngestionDate(String ingestionDate) {
        this.ingestionDate.set(javax.xml.bind.DatatypeConverter.parseDateTime(ingestionDate));
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public Calendar getIngestionDate() {
        return ingestionDate.get();
    }

    public ObjectProperty<Calendar> ingestionDateProperty() {
        return ingestionDate;
    }

    public void setIngestionDate(Calendar ingestionDate) {
        ingestionDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.ingestionDate.set(ingestionDate);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getFootprint() {
        return footprint.get();
    }

    public StringProperty footprintProperty() {
        return footprint;
    }

    public void setFootprint(String footprint) {
        this.footprint.set(footprint);
    }

    public String getSize() {
        return size.get();
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public String getProductType() {
        return productType.get();
    }

    public StringProperty productTypeProperty() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType.set(productType);
    }

    public String getPlatformName() {
        return platformName.get();
    }

    public StringProperty platformNameProperty() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName.set(platformName);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public double getSizeAsDouble() {
        if (size == null)
            return 0.0;
        double p;
        if (size.get().contains("MB"))
            return Double.parseDouble(size.get().substring(0,size.get().indexOf(" ")))/1024.0;
        else
            return Double.parseDouble(size.get().substring(0,size.get().indexOf(" ")));
    }

    @Override
    public String toString() {
        return "Product{" +
                "ingestionDate=" + ingestionDate +
                ", title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", footprint='" + footprint + '\'' +
                ", size='" + size + '\'' +
                ", productType='" + productType + '\'' +
                ", platformName='" + platformName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDTO)) return false;
        ProductDTO product = (ProductDTO) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

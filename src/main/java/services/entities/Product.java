package services.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.Calendar;
import java.util.Objects;

@Entity("products")
public class Product {
    protected String ingestionDate;
    protected String title;
    @Id
    protected String id;
    protected String footprint;
    protected String size;
    protected String productType;
    protected String platformName;
    protected String status;

    public Product(String id, String title, String platformName, String productType, String footprint, String size, String status, String ingestionDate) {
        //setIngestionDate(ingestionDate);
        this.id = id;
        this.title = title;
        this.footprint = footprint;
        this.size = size;
        this.productType = productType;
        this.platformName = platformName;
        this.status = status;
        this.ingestionDate = ingestionDate;
    }

    public Product() {
    }

    public String getIngestionDate() {
        return ingestionDate;
    }

    public void setIngestionDate(String ingestionDate) {
        this.ingestionDate = ingestionDate;
    }

    /*public void setIngestionDate(String ingestionDate) {
        this.ingestionDate = javax.xml.bind.DatatypeConverter.parseDateTime(ingestionDate);
    }*/

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFootprint(String footprint) {
        this.footprint = footprint;
    }

    public String getFootprint() {
        return footprint;
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
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

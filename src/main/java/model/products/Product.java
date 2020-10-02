package model.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import utils.OpenSearchProductDeserializer;

import java.util.Calendar;
import java.util.Objects;

@JsonDeserialize(using = OpenSearchProductDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private Calendar ingestionDate;
    protected String title;
    protected String id;
    private String footprint;
    private String size;
    private String productType;
    private String platformName;
    private String status;

    public Product() {
    }

    public Product(String ingestionDate, String title, String id, String footprint, String size, String productType, String platformName, String status) {
        setIngestionDate(ingestionDate);
        this.title = title;
        this.id = id;
        this.footprint = footprint;
        this.size = size;
        this.productType = productType;
        this.platformName = platformName;
        this.status = status;
    }

    public Calendar getIngestionDate() {
        return ingestionDate;
    }

    public void setIngestionDate(String ingestionDate) {
        this.ingestionDate = javax.xml.bind.DatatypeConverter.parseDateTime(ingestionDate);
    }

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

    public double getSizeAsDouble() {
        double p;
        if (size.contains("MB"))
            return Double.parseDouble(size.substring(0,size.indexOf(" ")))/1024.0;
        else
            return Double.parseDouble(size.substring(0,size.indexOf(" ")));
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

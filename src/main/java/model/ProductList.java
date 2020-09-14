package model;

import java.util.ArrayList;
import java.util.List;

public class ProductList {
    private String name;
    private String description;
    private List<ProductOData> productOData;

    public ProductList(String name, String description) {
        this.name = name;
        this.description = description;
        productOData = new ArrayList<>();
    }

    public List<ProductOData> getProducts() {
        return productOData;
    }

    public ProductOData getProductById(String id) {
        for (ProductOData productOData : this.productOData)
            if (productOData.getId().equals(id))
                return productOData;

        return null;
    }

    public int count() {
        return productOData.size();
    }

    public void addProduct(ProductOData productOData) {
        this.productOData.add(productOData);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ProductList {" +
                "name='" + name + '\'' +
                ", description='" + description;
    }
}

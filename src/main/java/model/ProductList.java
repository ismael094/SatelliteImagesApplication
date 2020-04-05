package model;

import java.util.ArrayList;
import java.util.List;

public class ProductList {
    private String name;
    private String description;
    private List<Product> products;

    public ProductList(String name, String description) {
        this.name = name;
        this.description = description;
        products = new ArrayList<>();
    }

    public List<Product> getProducts() {
        return products;
    }

    public Product getProductById(String id) {
        for (Product product : products)
            if (product.getId().equals(id))
                return product;

        return null;
    }

    public int count() {
        return products.size();
    }

    public void addProduct(Product product) {
        this.products.add(product);
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
}

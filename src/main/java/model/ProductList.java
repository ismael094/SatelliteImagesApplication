package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import model.products.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductList {
    private String name;
    private String description;
    private ObservableList<Product> products;

    public ProductList(String name, String description) {
        this.name = name;
        this.description = description;
        products = FXCollections.observableArrayList();
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    public Product getProductById(String id) {
        for (Product product : this.products)
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

    @Override
    public String toString() {
        return "ProductList {" +
                "name='" + name + '\'' +
                ", description='" + description;
    }

    public void remove(Product product) {
        products.remove(product);
    }
}

package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import model.openSearcher.SentinelProductParameters;
import model.products.Product;
import model.restriction.Restriction;

import java.util.*;
import java.util.stream.Collectors;

public class ProductList {
    private SimpleStringProperty name;
    private SimpleStringProperty description;
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final DoubleProperty sizeAsDouble = new SimpleDoubleProperty();
    private final IntegerProperty count = new SimpleIntegerProperty();
    private final List<Restriction> restrictions = new ArrayList<>();
    private final ObservableMap<String, String> areasOfWork = FXCollections.observableHashMap();

    public ProductList(SimpleStringProperty name, SimpleStringProperty description) {
        this.name = name;
        this.description = description;
    }

    public ProductList() {
    }

    public ObservableList<Product> getProducts() {
        return products;
    }



    public Product getProductById(String id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public int count() {
        return products.size();
    }

    public boolean addProduct(Product product) {
        if (product == null || products.contains(product) || !validate(product))
            return false;
        this.products.add(product);
        reloadProperties();
        return true;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    @Override
    public String toString() {
        return name.getValue();
    }

    public void remove(Product product) {
        products.remove(product);
        reloadProperties();
    }

    public void remove(Collection<Product> product) {
        products.removeAll(product);
        reloadProperties();
    }

    private void reloadProperties() {
        sizeAsDouble.setValue(productSize());
        count.setValue(products.size());
    }

    public double productSize() {
        return getProducts().stream()
                .mapToDouble(Product::getSizeAsDouble)
                .sum();
    }

    public double getSizeAsDouble() {
        return sizeAsDouble.get();
    }

    public DoubleProperty sizeAsDoubleProperty() {
        return sizeAsDouble;
    }

    public void setSizeAsDouble(double sizeAsDouble) {
        this.sizeAsDouble.set(sizeAsDouble);
    }

    public int getCount() {
        return count.get();
    }

    public IntegerProperty countProperty() {
        return count;
    }

    public void setCount(int count) {
        this.count.set(count);
    }

    public void addRestriction(Restriction restriction) {
        restrictions.add(restriction);
    }

    private boolean validate(Product product) {
        if (restrictions.size() == 0)
            return true;
        for (Restriction r : restrictions)
            if (!r.validate(product))
                return false;
        return true;
    }

    public String getAreaOfWork(String key) {
        return areasOfWork.getOrDefault(key,null);
    }

    public String getAreaOfWorkOrDefault(String key) {
        return areasOfWork.getOrDefault(key,areasOfWork.getOrDefault("default",null));
    }

    public void setDefaultAreaOfWork(String area) {
        this.areasOfWork.put("default",area);
    }

    public void setAreaOfWork(String id, String area) {
        this.areasOfWork.put(id,area);
    }

    public ObservableMap<String, String> getAreasOfWork() {
        return areasOfWork;
    }
}

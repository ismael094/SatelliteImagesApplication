package model.restriction;

import model.products.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductTypeRestriction implements Restriction{
    private final List<String> acceptedValues;

    public ProductTypeRestriction() {
        this.acceptedValues = new ArrayList<>();
    }

    @Override
    public void add(String restriction) {
        acceptedValues.add(restriction);
    }

    @Override
    public void remove(String restriction) {
        acceptedValues.remove(restriction);
    }

    @Override
    public boolean validate(Product product) {
        return acceptedValues.contains(product.getProductType());
    }
}

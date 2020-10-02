package model.restriction;

import model.openSearcher.SentinelProductParameters;
import model.products.Product;

import java.util.ArrayList;
import java.util.List;

public class PlatFormRestriction implements Restriction {
    private final List<String> acceptedValues;

    public PlatFormRestriction() {
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
        return acceptedValues.contains(product.getPlatformName());
    }
}

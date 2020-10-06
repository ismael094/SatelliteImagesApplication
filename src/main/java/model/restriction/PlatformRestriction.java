package model.restriction;

import dev.morphia.annotations.Embedded;
import model.products.ProductDTO;

import java.util.ArrayList;
import java.util.List;

@Embedded
public class PlatformRestriction implements Restriction {
    private final List<String> acceptedValues;

    public PlatformRestriction() {
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
    public boolean validate(ProductDTO product) {
        return acceptedValues.contains(product.getPlatformName());
    }

    @Override
    public List<String> getValues() {
        return acceptedValues;
    }
}

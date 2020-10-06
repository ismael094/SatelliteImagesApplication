package model.restriction;

import dev.morphia.annotations.Embedded;
import model.products.ProductDTO;

import java.util.List;

@Embedded
public interface Restriction {
    void add(String restriction);
    void remove(String restriction);
    boolean validate(ProductDTO product);
    List<String> getValues();
}

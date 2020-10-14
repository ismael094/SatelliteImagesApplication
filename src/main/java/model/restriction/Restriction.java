package model.restriction;

import model.products.ProductDTO;

import java.util.List;


public interface Restriction {
    void add(String restriction);
    void remove(String restriction);
    boolean validate(ProductDTO product);
    List<String> getValues();
}

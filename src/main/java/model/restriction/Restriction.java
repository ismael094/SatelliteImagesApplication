package model.restriction;

import model.products.Product;

public interface Restriction {
    void add(String restriction);
    void remove(String restriction);
    boolean validate(Product product);
}

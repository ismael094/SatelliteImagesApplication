package model.processing;

import model.list.ProductListDTO;
import model.products.ProductDTO;

public interface Processing {
    void process(ProductListDTO productList);
    void process(ProductDTO productList);
}

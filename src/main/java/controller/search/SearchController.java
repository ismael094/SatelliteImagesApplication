package controller.search;

import model.products.Product;

import java.util.List;

public interface SearchController {
    void search();
    List<Product> getSelectedProducts();
}

package controller.search;

import javafx.collections.ObservableList;
import model.products.Product;

public interface SearchController {
    void search();
    ObservableList<Product> getSelectedProducts();

    ObservableList<Product> getProducts();
}

package controller.search;

import javafx.collections.ObservableList;
import model.products.ProductDTO;

public interface SearchController {
    void search();
    ObservableList<ProductDTO> getSelectedProducts();
    ObservableList<ProductDTO> getProducts();
}

package controller.search;

import javafx.collections.ObservableList;
import model.openSearcher.ProductParameters;
import model.products.ProductDTO;

import java.util.Map;

public interface SearchController {
    void search();
    void setParameters(Map<String, String> parameters);
    Map<String, String> getParameters();
    ObservableList<ProductDTO> getSelectedProducts();
    ObservableList<ProductDTO> getProducts();
}

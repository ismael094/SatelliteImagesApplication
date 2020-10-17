package controller.search;

import javafx.collections.ObservableList;
import model.openSearcher.ProductParameters;
import model.products.ProductDTO;

import java.util.Map;

public interface SearchController {
    void search();
    void setParametersOfAllResponses(Map<String, String> parametersOfAllResponses);
    Map<String, String> getParametersOfAllResponses();
    ObservableList<ProductDTO> getSelectedProducts();
    ObservableList<ProductDTO> getProducts();
}

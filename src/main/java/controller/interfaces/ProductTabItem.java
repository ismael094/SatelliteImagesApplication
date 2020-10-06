package controller.interfaces;

import javafx.collections.ObservableList;
import model.list.ProductListDTO;
import model.products.ProductDTO;

public interface ProductTabItem {
    ProductListDTO getProductList();
    ObservableList<ProductDTO> getSelectedProducts();
}

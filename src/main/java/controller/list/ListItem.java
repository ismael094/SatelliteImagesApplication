package controller.list;

import javafx.collections.ObservableList;
import model.ProductList;
import model.products.Product;

public interface ListItem {
    ProductList getProductList();
    ObservableList<Product> getSelectedProducts();
}

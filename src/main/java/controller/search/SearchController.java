package controller.search;

import controller.interfaces.TabItem;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ObservableList;
import model.products.ProductDTO;

import java.util.Map;

public abstract class SearchController<T> implements TabItem {

    public abstract void search();
    public abstract void setSearchParameters(Map<String, String> parametersOfAllResponses);
    public abstract Map<String, String> getSearchParameters();
    public abstract ObservableList<T> getSelectedProducts();
    public abstract ObservableList<T> getProducts();
    public abstract void undo();
    public abstract void redo();
}

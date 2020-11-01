package model.openSearcher;

import model.products.ProductDTO;

import java.util.ArrayList;
import java.util.List;

public interface SearchResponse {
    int getNumOfProducts();

    void setNumOfProducts(int numOfProducts);

    List<Object> getProducts();

    void setProducts(List<Object> products);

    int getStartIndex();

    void setStartIndex(int startIndex);

    int getRows();

    void setRows(int rows);
}

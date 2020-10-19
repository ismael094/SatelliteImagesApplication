package services.processing;

import model.list.ProductListDTO;
import model.processing.Workflow;
import model.products.ProductDTO;

import java.util.List;

public interface Processing {
    void process(ProductListDTO productList);
    void process(ProductDTO productList, List<String> areasOfWork, Workflow workflow);
}

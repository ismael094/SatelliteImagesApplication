package controller.processing;

import model.list.ProductListDTO;
import services.processing.Processor;

public interface ProcessingMonitorController {
    void setProcessor(Processor processor);
    void setProductList(ProductListDTO productList);
}

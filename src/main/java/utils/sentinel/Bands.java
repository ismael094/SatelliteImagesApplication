package utils.sentinel;

import model.preprocessing.workflow.WorkflowDTO;
import model.products.ProductDTO;

import java.util.List;

public interface Bands {
    List<String> getBands(ProductDTO productDTO, WorkflowDTO workflowDTO);
}

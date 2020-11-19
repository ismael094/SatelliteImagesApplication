package utils.sentinel;

import model.preprocessing.workflow.WorkflowDTO;
import model.products.ProductDTO;

import java.util.List;

public interface Bands {
    /**
     * Get output bands of product with workflow
     * @param productDTO product
     * @param workflowDTO workflow
     * @return list of output bands
     */
    List<String> getBands(ProductDTO productDTO, WorkflowDTO workflowDTO);
}

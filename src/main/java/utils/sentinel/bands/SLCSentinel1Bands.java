package utils.sentinel.bands;

import model.processing.workflow.WorkflowDTO;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;

import java.util.Arrays;
import java.util.List;

public class SLCSentinel1Bands extends Sentinel1Bands {

    @Override
    public List<String> getBands(ProductDTO productDTO, WorkflowDTO workflowDTO) {
        return null;
    }
}

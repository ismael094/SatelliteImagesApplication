package utils;

import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import utils.sentinel.SentinelBands;

import java.util.List;

public class ProductBandUtils {
    public static List<String> getOutputBands(ProductDTO productDTO, WorkflowDTO workflowDTO) {
        return SentinelBands.getBands(productDTO,workflowDTO);
    }


}

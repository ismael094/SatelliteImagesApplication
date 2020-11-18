package utils;

import model.preprocessing.workflow.WorkflowDTO;
import model.products.ProductDTO;
import utils.sentinel.Bands;
import utils.sentinel.SentinelBands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductBandUtils {
    /**
     * get band maps per satellite
     * @return map with bands
     */
    private static Map<String, Bands> getMap() {
        Map<String, Bands> map = new HashMap<>();
        map.put("Sentinel-1",new SentinelBands());
        map.put("Sentinel-2",new SentinelBands());
        return map;
    }

    /**
     * get output bands of product with workflow
     * @param productDTO product
     * @param workflowDTO workflow
     * @return list of bands
     */
    public static List<String> getOutputBands(ProductDTO productDTO, WorkflowDTO workflowDTO) {
        return getMap().get(productDTO.getPlatformName()).getBands(productDTO,workflowDTO);
    }


}

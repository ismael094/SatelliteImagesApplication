package utils.sentinel;

import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.WorkflowType;
import model.products.ProductDTO;
import utils.sentinel.bands.GRDSentinel1Bands;
import utils.sentinel.bands.SLCSentinel1Bands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentinelBands implements Bands{

    private static Map<WorkflowType, Sentinel1Bands> getMap() {
        Map<WorkflowType, Sentinel1Bands> map = new HashMap<>();
        map.put(WorkflowType.GRD,new GRDSentinel1Bands());
        map.put(WorkflowType.SLC,new SLCSentinel1Bands());
        return map;
    }

    public List<String> getBands(ProductDTO sentinel1, WorkflowDTO workflowDTO) {
        return getMap().get(workflowDTO.getType()).getBands(sentinel1,workflowDTO);
    }
}

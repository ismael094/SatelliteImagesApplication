package utils.sentinel;

import model.processing.workflow.WorkflowType;
import services.entities.Workflow;
import utils.sentinel.bands.Bands;
import utils.sentinel.bands.GRDBands;
import utils.sentinel.bands.S2MSI1CBands;
import utils.sentinel.bands.SLCBands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductBands {

    private static Map<WorkflowType, Bands> getMap() {
        Map<WorkflowType, Bands> map = new HashMap<>();
        map.put(WorkflowType.GRD,new GRDBands());
        map.put(WorkflowType.SLC,new SLCBands());
        map.put(WorkflowType.S2MSI1C,new S2MSI1CBands());
        return map;
    }

    public static List<String> getInputBandsFor(WorkflowType type) {
        return getMap().get(type).getBands();
    }
}

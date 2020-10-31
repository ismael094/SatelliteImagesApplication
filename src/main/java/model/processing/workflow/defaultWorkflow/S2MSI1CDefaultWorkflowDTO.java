package model.processing.workflow.defaultWorkflow;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.processing.workflow.GeneralWorkflowDTO;
import model.processing.workflow.WorkflowType;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;

import java.util.HashMap;
import java.util.Map;

public class S2MSI1CDefaultWorkflowDTO extends GeneralWorkflowDTO {
    public S2MSI1CDefaultWorkflowDTO() {
        super(new SimpleStringProperty("Default S2MS workflow"),new SimpleObjectProperty<>(WorkflowType.S2MSI1C));
        getRead();
        getResampling();
        getSubset();
        getWrite();
    }

    private void getResampling() {
        Map<String, Object> parameters = new HashMap<>();
        //parameters.put("referenceBand","B2");
        parameters.put("targetResolution",10);
        parameters.put("upsampling","Nearest");
        parameters.put("downsampling","First");
        parameters.put("flagDownsampling","First");
        parameters.put("resampleOnPyramidLevels",true);
        addOperation(new Operation(Operator.RESAMPLE,parameters));
    }

    private void getWrite() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("formatName", "GeoTIFF");
        parameters.put("generatePNG", true);
        parameters.put("red", "B4");
        parameters.put("green", "B3");
        parameters.put("blue", "B2");
        addOperation(new Operation(Operator.WRITE,parameters));
    }

    private void getRead() {
        Map<String, Object> parameters = new HashMap<>();
        addOperation(new Operation(Operator.READ,parameters));
    }

    private void getSubset() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("copyMetadata", true);
        parameters.put("sourceBands", "B2,B3,B4,B5,B6,B7,B8,B8A,B11,B12");
        parameters.put("outputImageScaleInDb", true);
        addOperation(new Operation(Operator.SUBSET,parameters));
    }

    @Override
    public String toString() {
        return "Sentinel2GRDDefaultWorkflow{}";
    }
}

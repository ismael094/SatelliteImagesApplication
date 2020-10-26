package model.processing.workflow;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.processing.workflow.operation.Operator;
import model.processing.workflow.operation.Operation;

import java.util.HashMap;
import java.util.Map;

public class Sentinel1GRDDefaultWorkflowDTO extends GeneralWorkflowDTO {

    public Sentinel1GRDDefaultWorkflowDTO() {
        super(new SimpleStringProperty("Default GRD workflow"),new SimpleObjectProperty<>(WorkflowType.GRD));
        getRead();
        getThermalNoiseRemoval();
        getOrbit();
        getCalibration();
        getWriteAndRead("BEAM-DIMAP");
        getTerrainCorrection();
        getSubset();
        getWrite("GeoTIFF");
    }

    private void getWriteAndRead(String s) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("formatName", s);
        addOperation(new Operation(Operator.WRITE_AND_READ,parameters));
    }

    private void getOrbit() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orbitType", "Sentinel Precise (Auto Download)");
        parameters.put("polyDegree", 3);
        addOperation(new Operation(Operator.APPLY_ORBIT_FILE,parameters));
    }

    private void getThermalNoiseRemoval() {
        Map<String, Object> parameters = new HashMap<>();
        addOperation(new Operation(Operator.THERMAL_NOISE_REMOVAL,parameters));
    }

    private void getCalibration() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("outputBetaBand", false);
        parameters.put("outputSigmaBand", true);
        parameters.put("selectedPolarisations", "VV,VH");
        parameters.put("outputImageScaleInDb", false);
        parameters.put("sourceBands", "Intensity_VV,Intensity_VH");
        addOperation(new Operation(Operator.CALIBRATION,parameters));
    }

    private void getTerrainCorrection() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("demResamplingMethod", "NEAREST_NEIGHBOUR");
        parameters.put("imgResamplingMethod", "NEAREST_NEIGHBOUR");
        parameters.put("incidenceAngleForSigma0", "Use incidence angle from Ellipsoid");
        parameters.put("demName", "SRTM 3Sec");
        parameters.put("pixelSpacingInMeter", 10.0);
        parameters.put("nodataValueAtSea", false);
        parameters.put("sourceBands", "Sigma0_VH,Sigma0_VH");
        addOperation(new Operation(Operator.TERRAIN_CORRECTION,parameters));
    }

    private void getWrite(String formatName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("formatName", formatName);
        addOperation(new Operation(Operator.WRITE,parameters));
    }

    private void getRead() {
        Map<String, Object> parameters = new HashMap<>();
        addOperation(new Operation(Operator.READ,parameters));
    }

    private void getSubset() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("copyMetadata", true);
        parameters.put("sourceBands", "Sigma0_VH");
        parameters.put("outputImageScaleInDb", true);
        addOperation(new Operation(Operator.SUBSET,parameters));
    }

    @Override
    public String toString() {
        return "Sentinel1GRDDefaultWorkflow{}";
    }
}

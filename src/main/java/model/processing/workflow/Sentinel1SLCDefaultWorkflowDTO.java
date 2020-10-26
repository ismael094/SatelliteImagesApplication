package model.processing.workflow;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;

import java.util.HashMap;
import java.util.Map;

public class Sentinel1SLCDefaultWorkflowDTO extends GeneralWorkflowDTO {

    public Sentinel1SLCDefaultWorkflowDTO() {
        super(new SimpleStringProperty("Default GRD workflow"),new SimpleObjectProperty<>(WorkflowType.SLC));
        getRead();
        getTopSarSplit();
        getOrbit();
        getCalibration();
        getWriteAndRead("BEAM-DIMAP");
        getTopSarDeburst();
        getMultilook();
        getTerrainCorrection();
        getSubset();
        getWrite("GeoTIFF");
    }

    private void getTopSarSplit() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("subswath", "IW1");
        parameters.put("selectedPolarisations", "VV,VH");
        parameters.put("firstBurstIndex", "1");
        parameters.put("lastBurstIndex", "9");
        addOperation(new Operation(Operator.TOPSAR_SPLIT,parameters));
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

    private void getCalibration() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("auxFile", "Latest Auxiliary File");
        parameters.put("outputImageInComplex", true);
        parameters.put("outputImageScaleInDb", false);
        parameters.put("createGammaBand", false);
        parameters.put("createBetaBand", false);
        parameters.put("outputSigmaBand", true);
        parameters.put("outputGammaBand", true);
        parameters.put("outputBetaBand", false);
        addOperation(new Operation(Operator.CALIBRATION,parameters));
    }

    private void getTopSarDeburst() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("selectedPolarisations", "VV,VH");
        addOperation(new Operation(Operator.TOPSAR_DEBURST,parameters));
    }

    private void getMultilook() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nRgLooks", 4);
        parameters.put("nAzLooks", 1);
        parameters.put("outputIntensity", false);
        parameters.put("grSquarePixel", true);
        addOperation(new Operation(Operator.MULTILOOK,parameters));
    }

    private void getTerrainCorrection() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("demResamplingMethod", "NEAREST_NEIGHBOUR");
        parameters.put("imgResamplingMethod", "NEAREST_NEIGHBOUR");
        parameters.put("incidenceAngleForSigma0", "Use incidence angle from Ellipsoid");
        parameters.put("demName", "SRTM 3Sec");
        parameters.put("pixelSpacingInMeter", 10.0);
        parameters.put("nodataValueAtSea", false);
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
        parameters.put("outputImageScaleInDb", true);
        addOperation(new Operation(Operator.SUBSET,parameters));
    }

    @Override
    public String toString() {
        return "Sentinel1SLCDefaultWorkflow{}";
    }
}

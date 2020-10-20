package model.processing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Sentinel1GRDDefaultWorkflow implements Workflow {
    private final List<Operation> operations;

    public Sentinel1GRDDefaultWorkflow() {
        operations = new LinkedList<>();
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

    @Override
    public WorkflowType getType() {
        return WorkflowType.GRD;
    }

    @Override
    public String getName() {
        return "Sentinel 1 GRD Default";
    }

    @Override
    public List<Operation> getOperations() {
        return operations;
    }

    @Override
    public void addOperation(Operation operation) {
        operations.add(operation);
    }

    @Override
    public Operation getOperation(Operator operator) {
        return operations.stream()
                .filter(o->o.getName() == operator)
                .findAny()
                .orElse(null);
    }

    @Override
    public void removeOperation(Operation operation) {
        operations.remove(operation);
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
        parameters.put("outputBetaBand", true);
        parameters.put("selectedPolarisations", "VV,VH");
        parameters.put("outputImageScaleInDb", false);
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
        parameters.put("sourceBands", "Beta0_VH");
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
        parameters.put("sourceBands", "Beta0_VH");
        parameters.put("outputImageScaleInDb", true);
        addOperation(new Operation(Operator.SUBSET,parameters));
    }

    @Override
    public String toString() {
        return "Sentinel1GRDDefaultWorkflow{" +
                "operations=" + operations +
                '}';
    }
}

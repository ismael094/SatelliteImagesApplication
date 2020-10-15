package model.processing;

import model.products.ProductDTO;
import org.esa.snap.core.datamodel.Product;

import java.util.Map;

public abstract class Sentinel1Processing extends SentinelProcessing {


    public Product getThermalNoiseRemoval(Product product, Map<String, Object> parameters) {
        return createProduct(Operator.THERMAL_NOISE_REMOVAL,product,parameters);
    }

    public Product getApplyOrbitFile(Product product, Map<String, Object> parameters) {
        return createProduct(Operator.APPLY_ORBIT_FILE,product,parameters);
    }

    public Product getCalibration(Product product, Map<String, Object> parameters) {
        return createProduct(Operator.CALIBRATION,product,parameters);
    }

    public Product getBorderNoiseRemoval(Product product, Map<String, Object> parameters) {
        return createProduct(Operator.REMOVE_GRD_BORDER_NOISE,product,parameters);
    }

    public Product getTerrainCorrection(Product product, Map<String, Object> parameters) {
        return createProduct(Operator.TERRAIN_CORRECTION,product,parameters);
    }

    public Product getTerrainFlattening(Product product, Map<String, Object> parameters) {
        return createProduct(Operator.TERRAIN_FLATTENING,product,parameters);
    }

    protected Map<String, Object> getCalibrationParameters(Product product) {
        parameters.clear();
        parameters.put("outputBetaBand", true);
        parameters.put("sourceBands", getBandNames(product.getBandNames()));
        //parameters.put("selectedPolarisations", VV+","+VH);
        parameters.put("selectedPolarisations", "VV");
        parameters.put("outputImageScaleInDb", false);
        return parameters;
    }

    protected Map<String, Object> getTerrainFlatteningParameters(Product product) {
        parameters.clear();
        parameters.put("demResamplingMethod", "NEAREST_NEIGHBOUR");
        parameters.put("imgResamplingMethod", "NEAREST_NEIGHBOUR");
        parameters.put("incidenceAngleForSigma0", "Use incidence angle from Ellipsoid");
        parameters.put("demName", "SRTM 3Sec");
        parameters.put("pixelSpacingInMeter", 10.0);
        parameters.put("nodataValueAtSea", false);
        //parameters.put("sourceBands", "Sigma0_"+ VV+",Sigma0_"+VH);
        parameters.put("sourceBands", "Beta0_VV");
        return parameters;
    }

    protected Map<String, Object> getTerrainCorrectionParameters(Product product) {
        parameters.clear();
        parameters.put("demResamplingMethod", "NEAREST_NEIGHBOUR");
        parameters.put("demName", "SRTM 3Sec");
        //parameters.put("demName", "SRTM 1Sec HGT");
        //parameters.put("demName", "SRTM 1Sec HGT");
        parameters.put("sourceBands", getBandNames(product.getBandNames()));
        return parameters;
    }

    protected Map<String, Object> getThermalNoiseRemovalParameters() {
        parameters.clear();
        return parameters;
    }

}

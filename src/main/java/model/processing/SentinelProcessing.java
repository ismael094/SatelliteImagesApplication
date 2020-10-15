package model.processing;

import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class SentinelProcessing implements Processing {
    protected final Map<String,Object> parameters;

    public SentinelProcessing() {
        this.parameters = new HashMap<>();
    }

    protected Product readProduct(String path) throws IOException {
        return ProductIO.readProduct(path);
    }

    protected void saveProduct(Product product, String path, String formatName) throws IOException {
        ProductIO.writeProduct(product,path,formatName);
    }

    protected Product subset(Product product, Map<String, Object> parameters) {
        return createProduct(Operator.SUBSET,product,parameters);
    }

    protected String getBandNames(String[] bandNames) {
        return String.join(",", bandNames);
    }

    protected Product createProduct(Operator operator, Product product, Map<String, Object> parameters) {
        return GPF.createProduct(operator.getName(),parameters,product);
    }

    protected Map<String, Object> getSubsetParameters(String WKT) throws ParseException {
        parameters.clear();
        parameters.put("copyMetadata", true);
        parameters.put("geoRegion", new WKTReader().read(WKT));
        //parameters.put("sourceBands", "Sigma0_"+ VV+",Sigma0_"+VH);
        parameters.put("sourceBands", "Beta0_VV");
        parameters.put("outputImageScaleInDb", true);
        return parameters;
    }


    protected enum Operator {
        SUBSET("Subset"),
        THERMAL_NOISE_REMOVAL("ThermalNoiseRemoval"),
        APPLY_ORBIT_FILE("Apply-Orbit-File"),
        CALIBRATION("Calibration"),
        REMOVE_GRD_BORDER_NOISE("Remove-GRD-Border-Noise"),
        TERRAIN_CORRECTION("Terrain-Correction"),
        TERRAIN_FLATTENING("Terrain-Flattening");

        private final String name;

        Operator(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}

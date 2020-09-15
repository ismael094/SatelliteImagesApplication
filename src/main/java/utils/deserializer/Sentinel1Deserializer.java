package utils.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import model.products.Product;
import model.products.ProductProperties;
import model.products.Sentinel1Product;

import java.util.List;

public class Sentinel1Deserializer extends Deserializer {
    private final String SENSOR_OPERATIONAL_MODE = "sensoroperationalmode";
    private final String POLARISATION_MODE = "polarisationmode";
    @Override
    public Product deserialize(JsonNode product) {
        Sentinel1Product sentinel1 = new Sentinel1Product();
        List<ProductProperties> stringProperties = getStringProperties(product);
        sentinel1.setSensorOperationalMode((String)getPropertyByName(SENSOR_OPERATIONAL_MODE,stringProperties));
        sentinel1.setPolarizationMode((String)getPropertyByName(POLARISATION_MODE,stringProperties));
        setProductProperties(stringProperties,product,sentinel1);
        return sentinel1;
    }
}

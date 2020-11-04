package utils.deserializer.sentinel.impl;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductProperties;
import model.products.sentinel.Sentinel1ProductDTO;
import model.products.sentinel.SentinelProductDTO;

import java.util.List;

public class Sentinel1OpenSearchProductDeserializer extends SentinelOpenSearchProductDeserializer {
    private final String SENSOR_OPERATIONAL_MODE = "sensoroperationalmode";
    private final String POLARISATION_MODE = "polarisationmode";

    @Override
    public SentinelProductDTO deserialize(JsonNode product) {
        Sentinel1ProductDTO sentinel1 = new Sentinel1ProductDTO(
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleObjectProperty<>(),
                new SimpleStringProperty(),new SimpleStringProperty());

        List<ProductProperties> stringProperties = getStringProperties(product);

        sentinel1.setSensorOperationalMode((String)getPropertyByName(SENSOR_OPERATIONAL_MODE,stringProperties));
        sentinel1.setPolarizationMode((String)getPropertyByName(POLARISATION_MODE,stringProperties));
        setProductProperties(stringProperties,product,sentinel1);
        return sentinel1;
    }
}

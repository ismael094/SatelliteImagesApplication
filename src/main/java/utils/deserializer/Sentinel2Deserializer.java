package utils.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.ProductProperties;
import model.products.Sentinel2ProductDTO;

import java.util.List;

public class Sentinel2Deserializer extends Deserializer {
    public static final String CLOUD_COVER_PERCENTAGE = "cloudcoverpercentage";
    public static final String WATER_COVER_PERCENTAGE = "watercoverpercentage";
    public static final String NOT_VEGETATION_COVER_PERCENTAGE = "notvegetationcoverpercentage";
    public static final String VEGETATION_COVER_PERCENTAGE = "vegetationcoverpercentage";

    @Override
    public ProductDTO deserialize(JsonNode product) {
        Sentinel2ProductDTO sentinel2 =  new Sentinel2ProductDTO(
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleObjectProperty<>(),
                new SimpleDoubleProperty(),new SimpleDoubleProperty(),
                new SimpleDoubleProperty(),new SimpleDoubleProperty());

        List<ProductProperties> stringProperties = getStringProperties(product);
        if (product.get(DOUBLE_ARRAY).isArray()) {
            List<ProductProperties> doubleProperties = getDoubleProperties(product);
            setCloudCoverPercentageIfExistsPropertyIfExists(sentinel2, doubleProperties);
            setWaterCoverageIfExistsPropertyIfExists(sentinel2, doubleProperties);
            setNotVegetationPercentagePropertyIfExists(sentinel2, doubleProperties);
            setVegetationPercentageIfExistsPropertyIfExists(sentinel2, doubleProperties);
        } else {
            sentinel2.setCloudCoverPercentage(product.get(DOUBLE_ARRAY).get("content").asDouble());
        }
        setProductProperties(stringProperties,product,sentinel2);
        return sentinel2;
    }

    private void setNotVegetationPercentagePropertyIfExists(Sentinel2ProductDTO product, List<ProductProperties> doubleProperties) {
        if (getPropertyByName(NOT_VEGETATION_COVER_PERCENTAGE,doubleProperties) == null)
            return;
        product.setNotVegetationPercentageCoverage(Double.parseDouble((String)  getPropertyByName(NOT_VEGETATION_COVER_PERCENTAGE,doubleProperties)));
    }

    private void setVegetationPercentageIfExistsPropertyIfExists(Sentinel2ProductDTO product, List<ProductProperties> doubleProperties) {
        if (getPropertyByName(VEGETATION_COVER_PERCENTAGE,doubleProperties) == null)
            return;
        product.setVegetationPercentageCoverage(Double.parseDouble((String)  getPropertyByName(VEGETATION_COVER_PERCENTAGE,doubleProperties)));
    }

    private void setWaterCoverageIfExistsPropertyIfExists(Sentinel2ProductDTO product, List<ProductProperties> doubleProperties) {
        if (getPropertyByName(WATER_COVER_PERCENTAGE,doubleProperties) == null)
            return;
        product.setWaterPercentageCoverage(Double.parseDouble((String)  getPropertyByName(WATER_COVER_PERCENTAGE,doubleProperties)));
    }

    private void setCloudCoverPercentageIfExistsPropertyIfExists(Sentinel2ProductDTO product, List<ProductProperties> doubleProperties) {
        if (getPropertyByName(CLOUD_COVER_PERCENTAGE,doubleProperties) == null)
            return;
        product.setCloudCoverPercentage(Double.parseDouble((String) getPropertyByName(CLOUD_COVER_PERCENTAGE,doubleProperties)));
    }
}

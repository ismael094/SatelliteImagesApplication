package utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import model.Product;
import model.ProductProperties;
import model.Sentinel1Product;
import model.Sentinel2Product;

import java.io.IOException;
import java.util.List;

public class ProductDeserializer extends StdDeserializer<Product> {

    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String CLOUD_COVER_PERCENTAGE = "cloudcoverpercentage";
    public static final String WATER_COVER_PERCENTAGE = "watercoverpercentage";
    public static final String NOT_VEGETATION_COVER_PERCENTAGE = "notvegetationcoverpercentage";
    public static final String VEGETATION_COVER_PERCENTAGE = "vegetationcoverpercentage";
    public static final String DOUBLE_ARRAY = "double";
    public static final String FOOTPRINT = "footprint";
    public static final String SIZE = "size";
    public static final String PRODUCT_TYPE = "producttype";
    public static final String PLATFORM_NAME = "platformname";
    public static final String TITLE = "title";
    public static final String ID = "id";
    public static final String STR_ARRAY = "str";
    public static final String SENTINEL_1 = "Sentinel-1";
    public static final String SENTINEL_2 = "Sentinel-2";
    public static final String SENSOR_OPERATIONAL_MODE = "sensoroperationalmode";
    public static final String POLARISATION_MODE = "polarisationmode";
    TypeReference<List<ProductProperties>> typeRef = new TypeReference<>() {};
    TypeReference<List<ProductProperties>> typeRefD = new TypeReference<>() {};


    public ProductDeserializer() {
        this(null);
    }

    public ProductDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Product deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);

        List<ProductProperties> stringProperties = mapper.convertValue(node.get(STR_ARRAY), typeRef);

        Product product = new Product();

        if (isSentinel1Plataform(stringProperties)) {
            product = getSentinel1Product(stringProperties);
        } else if (isSentinel2Plataform(stringProperties)) {
            product = getSentinel2Product(node);
        }

        setProductProperties(stringProperties,node, product);

        return product;
    }

    private void setProductProperties(List<ProductProperties> stringProperties, JsonNode node, Product product) {
        product.setTitle(node.get(TITLE).asText());
        product.setId(node.get(ID).asText());
        product.setFootprint((String)getPropertyByName(FOOTPRINT,stringProperties));
        product.setSize((String)getPropertyByName(SIZE,stringProperties));
        product.setProductType((String)getPropertyByName(PRODUCT_TYPE,stringProperties));
        product.setPlatformName((String)getPropertyByName(PLATFORM_NAME,stringProperties));
    }

    private boolean isSentinel1Plataform(List<ProductProperties> stringProperties) {
        return ((getPropertyByName(PLATFORM_NAME, stringProperties)).equals(SENTINEL_1));
    }

    private boolean isSentinel2Plataform(List<ProductProperties> stringProperties) {
        return ((getPropertyByName(PLATFORM_NAME, stringProperties)).equals(SENTINEL_2));
    }

    private Sentinel1Product getSentinel1Product(List<ProductProperties> stringProperties) {
        Sentinel1Product product = new Sentinel1Product();
        product.setSensorOperationalMode((String)getPropertyByName(SENSOR_OPERATIONAL_MODE,stringProperties));
        product.setPolarizationMode((String)getPropertyByName(POLARISATION_MODE,stringProperties));
        return product;
    }

    private Sentinel2Product getSentinel2Product(JsonNode node) {
        Sentinel2Product product = new Sentinel2Product();
        setSentinel2Data(node, product);
        return product;
    }

    private void setSentinel2Data(JsonNode node, Sentinel2Product product) {
        if (node.get(DOUBLE_ARRAY).isArray()) {
            List<ProductProperties> doubleProperties = mapper.convertValue(node.get(DOUBLE_ARRAY), typeRefD);
            setCloudCoverPercentageIfExistsPropertyIfExists(product, doubleProperties);
            setWaterCoverageIfExistsPropertyIfExists(product, doubleProperties);
            setNotVegetationPercentagePropertyIfExists(product, doubleProperties);
            setVegetationPercentageIfExistsPropertyIfExists(product, doubleProperties);
        } else {
            product.setCloudCoverPercentage(node.get(DOUBLE_ARRAY).get("content").asDouble());
        };
    }

    private void setNotVegetationPercentagePropertyIfExists(Sentinel2Product product, List<ProductProperties> doubleProperties) {
        if (getPropertyByName(NOT_VEGETATION_COVER_PERCENTAGE,doubleProperties) == null)
            return;
        product.setNotVegetationPercentageCoverage(Double.parseDouble((String)  getPropertyByName(NOT_VEGETATION_COVER_PERCENTAGE,doubleProperties)));
    }

    private void setVegetationPercentageIfExistsPropertyIfExists(Sentinel2Product product, List<ProductProperties> doubleProperties) {
        if (getPropertyByName(VEGETATION_COVER_PERCENTAGE,doubleProperties) == null)
            return;
        product.setVegetationPercentageCoverage(Double.parseDouble((String)  getPropertyByName(VEGETATION_COVER_PERCENTAGE,doubleProperties)));
    }

    private void setWaterCoverageIfExistsPropertyIfExists(Sentinel2Product product, List<ProductProperties> doubleProperties) {
        if (getPropertyByName(WATER_COVER_PERCENTAGE,doubleProperties) == null)
            return;
        product.setWaterPercentageCoverage(Double.parseDouble((String)  getPropertyByName(WATER_COVER_PERCENTAGE,doubleProperties)));
    }

    private void setCloudCoverPercentageIfExistsPropertyIfExists(Sentinel2Product product, List<ProductProperties> doubleProperties) {
        if (getPropertyByName(CLOUD_COVER_PERCENTAGE,doubleProperties) == null)
            return;
        product.setCloudCoverPercentage(Double.parseDouble((String) getPropertyByName(CLOUD_COVER_PERCENTAGE,doubleProperties)));
    }

    public Object getPropertyByName(String property, List<ProductProperties> properties) {
        return properties.stream()
                .filter(e->e.getName().equals(property))
                .map(ProductProperties::getContent)
                .findFirst()
                .orElse(null);
    }


}

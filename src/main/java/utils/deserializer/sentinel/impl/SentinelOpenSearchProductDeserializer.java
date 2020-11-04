package utils.deserializer.sentinel.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.products.ProductDTO;
import model.products.ProductProperties;
import services.CopernicusService;

import java.io.IOException;
import java.util.List;

import static java.lang.System.currentTimeMillis;

public abstract class SentinelOpenSearchProductDeserializer {
    public static final String DOUBLE_ARRAY = "double";
    public static final String STR_ARRAY = "str";
    public static final String FOOTPRINT = "footprint";
    public static final String SIZE = "size";
    public static final String PRODUCT_TYPE = "producttype";
    public static final String PLATFORM_NAME = "platformname";
    public static final String TITLE = "title";
    public static final String ID = "id";

    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String DATE_ARRAY = "date";
    public static final String INGESTION_DATE = "ingestiondate";

    TypeReference<List<ProductProperties>> typeRef = new TypeReference<List<ProductProperties>>() {};

    public List<ProductProperties> getStringProperties(JsonNode product) {
        return mapper.convertValue(product.get(STR_ARRAY), typeRef);
    }

    public List<ProductProperties> getDoubleProperties(JsonNode product) {
        return mapper.convertValue(product.get(DOUBLE_ARRAY), typeRef);
    }

    public List<ProductProperties> getDateProperties(JsonNode product) {
        return mapper.convertValue(product.get(DATE_ARRAY), typeRef);
    }

    public Object getPropertyByName(String property, List<ProductProperties> properties) {
        return properties.stream()
                .filter(e->e.getName().equals(property))
                .map(ProductProperties::getContent)
                .findFirst()
                .orElse(null);
    }

    protected void setProductProperties(List<ProductProperties> stringProperties, JsonNode node, ProductDTO product) {
        product.setTitle(node.get(TITLE).asText());
        product.setId(node.get(ID).asText());
        product.setFootprint((String)getPropertyByName(FOOTPRINT,stringProperties));
        product.setSize((String)getPropertyByName(SIZE,stringProperties));
        product.setProductType((String)getPropertyByName(PRODUCT_TYPE,stringProperties));
        product.setPlatformName((String)getPropertyByName(PLATFORM_NAME,stringProperties));
        product.setIngestionDate((String)getPropertyByName(INGESTION_DATE,getDateProperties(node)));
    }

    public abstract ProductDTO deserialize(JsonNode product);
}

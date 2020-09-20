package utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import model.products.Product;
import model.products.ProductProperties;
import model.products.Sentinel1Product;
import model.products.Sentinel2Product;
import utils.deserializer.DefaultDeserializer;
import utils.deserializer.Deserializer;
import utils.deserializer.Sentinel1Deserializer;
import utils.deserializer.Sentinel2Deserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSearchProductDeserializer extends StdDeserializer<Product> {
    public static final String SENTINEL1 = "S1";
    public static final String SENTINEL2 = "S2";
    public static final String DEFAULT_DESERIALIZER = "default";
    Map<String, Deserializer> deserializerMap;

    public OpenSearchProductDeserializer() {
        this(null);
    }

    public OpenSearchProductDeserializer(Class<?> vc) {
        super(vc);
        initMap();
    }

    private void initMap() {
        deserializerMap = new HashMap<>();
        deserializerMap.put(SENTINEL1,new Sentinel1Deserializer());
        deserializerMap.put(SENTINEL2,new Sentinel2Deserializer());
        deserializerMap.put(DEFAULT_DESERIALIZER,new DefaultDeserializer());

    }

    @Override
    public Product deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);
        String name = node.get("title").asText().substring(0,2);

        Product product;

        if (name.startsWith(SENTINEL1)) {
            product = deserializerMap.get(SENTINEL1).deserialize(node);
        } else if (name.startsWith(SENTINEL2)) {
            product = deserializerMap.get(SENTINEL2).deserialize(node);
        } else {
            product = deserializerMap.get(DEFAULT_DESERIALIZER).deserialize(node);
        }

        return product;
    }
}

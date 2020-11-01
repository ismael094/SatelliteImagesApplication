package utils.deserializer.openSearchProductDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import model.products.ProductDTO;
import services.entities.Sentinel1Product;
import utils.SatelliteHelper;
import utils.deserializer.OpenSearchDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SentinelOpenSearchDeserializer extends StdDeserializer<ProductDTO>  {

    public static final String SENTINEL1 = "S1";

    public static final String SENTINEL2 = "S2";

    private Map<String,Class<? extends OpenSearchDeserializer>> deserializerMap;

    static Map<String,Class<? extends OpenSearchDeserializer>> getMap() {
        Map<String,Class<? extends OpenSearchDeserializer>> deserializerMap = new HashMap<>();
        deserializerMap.put(SENTINEL1,Sentinel1OpenSearchDeserializer.class);
        deserializerMap.put(SENTINEL2,Sentinel2OpenSearchDeserializer.class);
        return deserializerMap;
    }

    public SentinelOpenSearchDeserializer() {
        this(null);
    }

    protected SentinelOpenSearchDeserializer(Class<?> vc) {
        super(vc);
    }

    public static OpenSearchDeserializer getDeserializer(String name) {
        try {
            return getMap().getOrDefault(name, DefaultSentinelOpenSearchDeserializer.class).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return new DefaultSentinelOpenSearchDeserializer();
        }
    }

    @Override
    public ProductDTO deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        String name = node.get("title").asText().substring(0,2);
        return getDeserializer(name).deserialize(node);
    }
}

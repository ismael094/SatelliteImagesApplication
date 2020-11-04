package utils.deserializer.sentinel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import model.products.ProductDTO;
import utils.deserializer.sentinel.impl.DefaultSentinelOpenSearchProductDeserializer;
import utils.deserializer.sentinel.impl.Sentinel1OpenSearchProductDeserializer;
import utils.deserializer.sentinel.impl.Sentinel2OpenSearchProductDeserializer;
import utils.deserializer.sentinel.impl.SentinelOpenSearchProductDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SentinelOpenSearchDeserializerManager extends StdDeserializer<ProductDTO>  {

    public static final String SENTINEL1 = "S1";

    public static final String SENTINEL2 = "S2";

    private Map<String,Class<? extends SentinelOpenSearchProductDeserializer>> deserializerMap;

    static Map<String,Class<? extends SentinelOpenSearchProductDeserializer>> getMap() {
        Map<String,Class<? extends SentinelOpenSearchProductDeserializer>> deserializerMap = new HashMap<>();
        deserializerMap.put(SENTINEL1, Sentinel1OpenSearchProductDeserializer.class);
        deserializerMap.put(SENTINEL2, Sentinel2OpenSearchProductDeserializer.class);
        return deserializerMap;
    }

    public SentinelOpenSearchDeserializerManager() {
        this(null);
    }

    protected SentinelOpenSearchDeserializerManager(Class<?> vc) {
        super(vc);
    }

    public static SentinelOpenSearchProductDeserializer getDeserializer(String name) {
        try {
            return getMap().getOrDefault(name, DefaultSentinelOpenSearchProductDeserializer.class).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return new DefaultSentinelOpenSearchProductDeserializer();
        }
    }

    @Override
    public ProductDTO deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        String name = node.get("title").asText().substring(0,2);
        return getDeserializer(name).deserialize(node);
    }
}

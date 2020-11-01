package utils.deserializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.openSearcher.OpenSearchResponse;

import java.io.IOException;
import java.io.InputStream;

public class OpenSearchProductDeserializer implements ProductDeserializer {
    public static final String SENTINEL1 = "S1";
    public static final String SENTINEL2 = "S2";


    @Override
    public OpenSearchResponse deserialize(InputStream content) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        JsonNode jsonNode = mapper.readTree(content);
        JsonNode rootNode = jsonNode.path("feed");
        return mapper.treeToValue(rootNode, OpenSearchResponse.class);
    }
}

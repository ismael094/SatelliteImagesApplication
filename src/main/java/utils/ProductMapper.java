package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.openSearcher.OpenSearchResponse;

import java.io.IOException;
import java.io.InputStream;

public class ProductMapper {

    public static OpenSearchResponse getResponse(InputStream content) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        JsonNode jsonNode = mapper.readTree(content);
        JsonNode rootNode = jsonNode.path("feed");
        return mapper.treeToValue(rootNode, OpenSearchResponse.class);
    }
}

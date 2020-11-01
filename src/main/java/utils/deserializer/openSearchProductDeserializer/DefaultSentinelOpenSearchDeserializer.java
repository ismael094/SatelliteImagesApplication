package utils.deserializer.openSearchProductDeserializer;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.ProductProperties;
import model.products.sentinel.SentinelProductDTO;
import utils.deserializer.OpenSearchDeserializer;

import java.util.List;

public class DefaultSentinelOpenSearchDeserializer extends OpenSearchDeserializer {

    @Override
    public ProductDTO deserialize(JsonNode product) {
        ProductDTO pr = new SentinelProductDTO(
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleObjectProperty<>());
        List<ProductProperties> stringProperties = getStringProperties(product);
        setProductProperties(stringProperties,product,pr);
        return pr;
    }
}

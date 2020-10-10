package utils.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.ProductProperties;
import model.products.SentinelProductDTO;

import java.util.List;

public class DefaultSentinelDeserializer extends Deserializer {

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

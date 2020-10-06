package utils.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.ProductProperties;

import java.util.List;

public class DefaultDeserializer extends Deserializer {

    @Override
    public ProductDTO deserialize(JsonNode product) {
        ProductDTO pr = new ProductDTO(
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleStringProperty(),
                new SimpleStringProperty(),new SimpleObjectProperty<>());
        List<ProductProperties> stringProperties = getStringProperties(product);
        setProductProperties(stringProperties,product,pr);
        return pr;
    }
}

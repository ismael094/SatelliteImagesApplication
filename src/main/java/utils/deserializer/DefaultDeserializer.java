package utils.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import model.products.Product;
import model.products.ProductProperties;
import model.products.Sentinel1Product;

import java.util.List;

public class DefaultDeserializer extends Deserializer {

    @Override
    public Product deserialize(JsonNode product) {
        Product pr = new Product();
        List<ProductProperties> stringProperties = getStringProperties(product);
        setProductProperties(stringProperties,product,pr);
        return pr;
    }
}

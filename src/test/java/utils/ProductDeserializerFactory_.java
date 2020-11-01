package utils;

import org.junit.Test;
import utils.deserializer.NullProductDeserializer;
import utils.deserializer.ProductDeserializer;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDeserializerFactory_ {

    @Test
    public void get_open_search_deserializer() {
        ProductDeserializer openSearch = ProductDeserializerFactory.get("OpenSearch");
        assertThat(openSearch).isNotNull();
    }

    @Test
    public void get_null_deserializer() {
        ProductDeserializer openSearch = ProductDeserializerFactory.get("dddd");
        assertThat(openSearch).isInstanceOf(NullProductDeserializer.class);
    }
}

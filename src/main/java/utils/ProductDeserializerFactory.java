package utils;

import utils.deserializer.NullProductDeserializer;
import utils.deserializer.ProductDeserializer;

public class ProductDeserializerFactory {

    /**
     * Get deserializer with name
     * @param name name of deserializer
     * @return deserializer
     */
    public static ProductDeserializer get(String name) {
        try {
            Class<?> class_ = Class.forName("utils.deserializer."+name+"ProductDeserializer");
            return (ProductDeserializer) class_.newInstance();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | NoClassDefFoundError e) {
            return new NullProductDeserializer();
        }
    }
}

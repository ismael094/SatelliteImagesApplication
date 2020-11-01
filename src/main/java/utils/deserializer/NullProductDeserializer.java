package utils.deserializer;

import utils.deserializer.ProductDeserializer;

import java.io.IOException;
import java.io.InputStream;

public class NullProductDeserializer implements ProductDeserializer {
    @Override
    public Object deserialize(InputStream content) throws IOException {
        return null;
    }
}

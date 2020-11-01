package utils.deserializer;

import java.io.IOException;
import java.io.InputStream;

public interface ProductDeserializer {
    Object deserialize(InputStream content) throws IOException;
}

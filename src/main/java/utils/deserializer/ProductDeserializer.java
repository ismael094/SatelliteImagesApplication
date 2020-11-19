package utils.deserializer;

import java.io.IOException;
import java.io.InputStream;

public interface ProductDeserializer {
    /**
     * Deserialize InputStream
     * @param content InputStream
     * @return Response object with products
     * @throws IOException Error while deserialize
     */
    Object deserialize(InputStream content) throws IOException;
}

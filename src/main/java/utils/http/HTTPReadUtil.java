package utils.http;

import java.io.*;

public class HTTPReadUtil {
    public static String readFromURL(InputStream inputStream) throws IOException {
        String res = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            res = in.readLine();
        }
        return res;
    }
}

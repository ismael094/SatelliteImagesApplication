package utils;

import model.exception.AuthenticationException;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.currentTimeMillis;

public class HTTPAuthManager extends Authenticator {

    private final String username;
    private final String password;

    private static HTTPAuthManager httpManager = null;

    public HTTPAuthManager(String username, String password) {
        this.username = username;
        this.password = password;
        setAuthenticator();
    }

    public static HTTPAuthManager getHttpManager(String username, String password) {
        if (httpManager == null) {
            httpManager = new HTTPAuthManager(username, password);
            httpManager.setAuthenticator();
        }
            return httpManager;
    }

    public InputStream getContentFromURL(URL url) throws IOException, AuthenticationException {
        //setAuthenticator();
        URL path = new URL(url.toString().replace(" ", "%20"));
        System.out.println(path);
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) path.openConnection();
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 RuxitSynthetic/1.0 v6418838628 t38550 ath9b965f92 altpub");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            if (connection.getResponseCode() != 200)
                throw new AuthenticationException("Incorrect username or password");
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.INFO, "Connection", connection.getURL().toString());
            System.out.println(connection.getURL().toString());
            InputStream a = connection.getInputStream();
            return a;
        } finally {
            if (connection != null) {
                //connection.disconnect();
            }
        }
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }

    private void setAuthenticator() {
        Authenticator.setDefault(new BasicAuthenticator());
    }

    private final class BasicAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
}

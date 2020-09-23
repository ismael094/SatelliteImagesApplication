package utils;

import model.exception.AuthenticationException;
import org.apache.http.client.HttpResponseException;
import org.apache.logging.log4j.LogManager;

import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;

public class HTTPAuthManager extends Authenticator {

    private String username;
    private String password;

    @Singleton
    private static HTTPAuthManager httpManager = null;
    private HttpsURLConnection connection;


    static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(HTTPAuthManager.class.getName());

    public HTTPAuthManager(String username, String password) {
        setCredentials(username, password);
        setAuthenticator();
    }

    public static HTTPAuthManager getHttpManager(String username, String password) {
        if (httpManager == null) {
            httpManager = new HTTPAuthManager(username, password);
            httpManager.setAuthenticator();
        }
        httpManager.setCredentials(username, password);
        return httpManager;
    }

    private void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public InputStream getContentFromURL(URL url) throws IOException, AuthenticationException {
        setAuthenticator();
        URL path = new URL(url.toString().replace(" ", "%20"));
        connection = null;
        System.out.println(path.toString());
        connection = (HttpsURLConnection) path.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 RuxitSynthetic/1.0 v6418838628 t38550 ath9b965f92 altpub");
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-length", "0");
        System.out.println(connection.getResponseCode());
        if (connection.getResponseCode() == 500) {
            logger.atWarn().log("URL respond with {} code, {}",connection.getResponseCode(),path);
            throw new HttpResponseException(500,"Image not available");
        }
        if (connection.getResponseCode() != 200) {
            logger.atWarn().log("URL respond with {} code, login error? in {}",connection.getResponseCode(),path);
            throw new AuthenticationException("Incorrect username or password");
        }
        return connection.getInputStream();

    }

    public void closeConnection() {
        connection.disconnect();
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

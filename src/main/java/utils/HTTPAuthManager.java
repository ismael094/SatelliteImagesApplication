package utils;

import model.exception.AuthenticationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

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
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.getResponseCode();
            if (connection.getResponseCode() != 200)
                throw new AuthenticationException("Incorrect username or password");
            return connection.getInputStream();
        } finally {
            if (connection != null) {
                connection.disconnect();
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

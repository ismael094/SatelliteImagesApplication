package utils.http;

import model.exception.AuthenticationException;
import org.apache.http.client.HttpResponseException;
import org.apache.logging.log4j.LogManager;

import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;

public class CopernicusHTTPAuthManager extends Authenticator implements HTTPAuthManager {

    private String username;
    private String password;


    private HttpsURLConnection connection;

    @Singleton
    private static CopernicusHTTPAuthManager httpManager;
    static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(CopernicusHTTPAuthManager.class.getName());

    private CopernicusHTTPAuthManager(String username, String password) throws AuthenticationException {
        setCredentials(username, password);
        setAuthenticator();
        login();
    }

    public static CopernicusHTTPAuthManager getNewHttpManager(String username, String password) throws AuthenticationException {
        return new CopernicusHTTPAuthManager(username,password);
    }

    @Override
    public void login() throws AuthenticationException {
        try {
            String LOGIN_URL = "https://scihub.copernicus.eu/dhus/search?q=*&rows=0&format=json";
            getContentFromURL(new URL(LOGIN_URL));
        } catch (IOException e) {
            logger.atInfo().log("Not able to connect to SciHub API: {0}",e);
            e.printStackTrace();
        }
    }

    @Override
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public InputStream getContentFromURL(URL url) throws IOException, AuthenticationException {
        setAuthenticator();
        URL path = new URL(url.toString().replace(" ", "%20"));
        connection = null;
        connection = (HttpsURLConnection) path.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 RuxitSynthetic/1.0 v6418838628 t38550 ath9b965f92 altpub");
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-length", "0");
        isConnectionResponseOK(connection.getResponseCode());
        return connection.getInputStream();

    }

    @Override
    public void closeConnection() {
        connection.disconnect();
    }

    private void isConnectionResponseOK(int responseCode) throws HttpResponseException, AuthenticationException {
        if (responseCode == 401) {
            logger.atWarn().log("URL respond with {} code, login error?",responseCode);
            throw new AuthenticationException("Incorrect username or password");
        } else if (responseCode != 200) {
            logger.atWarn().log("URL respond with {} code",responseCode);
            throw new HttpResponseException(responseCode,"Resource not available");
        }
    }

    private void setAuthenticator() {
        Authenticator.setDefault(this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }

    /*private final class BasicAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }*/
}

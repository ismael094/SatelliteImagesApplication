package utils.http;

import model.exception.AuthenticationException;
import org.apache.http.client.HttpResponseException;
import org.apache.logging.log4j.LogManager;

import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class CopernicusHTTPAuthManager extends Authenticator implements HTTPAuthManager {

    private String username;
    private String password;


    private HttpsURLConnection connection;
    private Map<Integer,String> errors;

    @Singleton
    private static CopernicusHTTPAuthManager httpManager;
    static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(CopernicusHTTPAuthManager.class.getName());

    private CopernicusHTTPAuthManager(String username, String password) throws AuthenticationException {
        setCredentials(username, password);
        setAuthenticator();
        login();
        System.setProperty("http.keepAlive", "false");
        errors = new HashMap<>();
        errors.put(429,"Too many request");
        errors.put(403,"Maximum number of 2 concurrent flows achieved");
    }

    public static CopernicusHTTPAuthManager getNewHttpManager(String username, String password) throws AuthenticationException {
        return new CopernicusHTTPAuthManager(username,password);
    }

    public static CopernicusHTTPAuthManager getHttpManager(String username, String password) throws AuthenticationException {

        if (httpManager == null)
            httpManager = new CopernicusHTTPAuthManager(username,password);
        return httpManager;
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
        return getConnectionFromURL(url).getInputStream();
    }

    @Override
    public HttpsURLConnection getConnectionFromURL(URL url) throws IOException, AuthenticationException {
        setAuthenticator();
        URL path = new URL(url.toString().replace(" ", "%20"));
        connection = null;
        connection = (HttpsURLConnection) path.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
        connection.setConnectTimeout(90000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 RuxitSynthetic/1.0 v6418838628 t38550 ath9b965f92 altpub");
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-length", "0");
        connection.connect();
        isConnectionResponseOK(connection);
        return connection;
    }

    @Override
    public void closeConnection() {
        connection.disconnect();
    }

    private void isConnectionResponseOK(HttpsURLConnection connection) throws IOException, AuthenticationException {

        if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            logger.atWarn().log("URL respond with {} code, login error?",connection.getResponseCode());
            closeConnection();
            throw new AuthenticationException("Incorrect username or password");
        } else if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            logger.atWarn().log("URL respond with {} code {}",connection.getResponseCode(),errors.getOrDefault(connection.getResponseCode(),""));
            connection.getInputStream().close();
            closeConnection();
            throw new HttpResponseException(connection.getResponseCode(),errors.getOrDefault(connection.getResponseCode(),"Resource not available"));
        }
    }

    private void setAuthenticator() {
        Authenticator.setDefault(this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
    }
}

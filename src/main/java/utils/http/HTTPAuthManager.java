package utils.http;

import model.exception.AuthenticationException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface HTTPAuthManager {

    /**
     * login in HTTP
     * @throws AuthenticationException not login
     */
    void login() throws AuthenticationException;

    /**
     * save credentials
     * @param username username
     * @param password password
     */
    void setCredentials(String username, String password);

    /**
     * get content from url
     * @param url url
     * @return inputStream with content
     * @throws IOException error while connection to URL
     * @throws AuthenticationException not login
     */
    InputStream getContentFromURL(URL url) throws IOException, AuthenticationException;

    /**
     * get connection to url
     * @param url url
     * @return inputStream with content
     * @throws IOException error while connection to URL
     * @throws AuthenticationException not login
     */
    HttpsURLConnection getConnectionFromURL(URL url) throws IOException, AuthenticationException;

    /**
     * close connection
     */
    void closeConnection();
}

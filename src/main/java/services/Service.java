package services;

import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import utils.http.CopernicusHTTPAuthManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public interface Service {

    void login() throws AuthenticationException, NotAuthenticatedException;

    InputStream getContentFromURL(URL url) throws IOException, AuthenticationException, NotAuthenticatedException;

    HttpURLConnection getConnectionFromURL(URL url) throws IOException, AuthenticationException, NotAuthenticatedException;

    boolean isProductOnline(String id) throws IOException, AuthenticationException, NotAuthenticatedException;

    String getMD5CheckSum(String id) throws IOException, AuthenticationException, NotAuthenticatedException;

}

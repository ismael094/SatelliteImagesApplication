package services;

import gui.dialog.ScihubCredentialsDialog;
import javafx.util.Pair;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.entities.Product;
import utils.http.CopernicusHTTPAuthManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import static utils.http.HTTPReadUtil.readFromURL;

/**
 * Service to get access in Copernicus Open Access Hub using API.
 * Singleton class, if instance is not created, it shows a dialog
 * to enter credentials.
 * Be careful, login() must be called inside in JavaFX Thread
 * JavaFX Thread
 */
public class CopernicusService implements Service {

    static final Logger logger = LogManager.getLogger(CopernicusService.class.getName());
    private static CopernicusService instance;

    private CopernicusHTTPAuthManager httpManager;
    private Pair<String, String> pair;

    private CopernicusService() {
        ScihubCredentialsDialog dialog = new ScihubCredentialsDialog();
        Optional<Pair<String, String>> stringStringPair = dialog.showAndWait();
        stringStringPair.ifPresent(stringPair -> this.pair = stringPair);
        logger.atInfo().log("New CopernicusService open");
    }

    /**
     * Get instance of Service. If not logged, show credentials dialog. MUST BE CALLED IN JAVAFX THREAD
     * @return CopernicusService
     */
    public static CopernicusService getInstance() {
        if (instance == null || instance.httpManager == null)
            instance = new CopernicusService();
        return instance;
    }

    /**
     * Login in the Copernicus API. MUST BE CALLED INSIDE TASK.
     * If credentials are OK, logged in API. If not, throws AuthenticationException
     * @throws AuthenticationException if credentials are wrong
     * @throws NotAuthenticatedException if credentials are not setted
     */
    @Override
    public synchronized void login() throws AuthenticationException, NotAuthenticatedException {
        if (pair == null) {
            instance = null;
            logger.atWarn().log("Incorrect credentials while login in Copernicus API");
            throw new NotAuthenticatedException("Not authenticated");
        }
        httpManager = CopernicusHTTPAuthManager.getNewHttpManager(pair.getKey(),pair.getValue());
    }

    /**
     * Get content of resource of URL. To use this method you should be outside JavaFX Thread
     * @param url URL of API resource
     * @return Content as inputstream
     * @throws IOException error reading content from URL
     * @throws AuthenticationException if credentials are wrong
     * @throws NotAuthenticatedException no credentials setted
     */
    @Override
    public InputStream getContentFromURL(URL url) throws IOException, AuthenticationException, NotAuthenticatedException {
        if (httpManager == null)
            throw new NotAuthenticatedException("Not authenticated");
        return CopernicusHTTPAuthManager.getNewHttpManager(pair.getKey(),pair.getValue()).getContentFromURL(url);
    }

    /**
     * Get HttpURL connection from URL
     * @param url URL of API resource
     * @return Content as inputstream
     * @throws IOException error reading content from URL
     * @throws AuthenticationException if credentials are wrong
     * @throws NotAuthenticatedException no credentials setted
     */
    @Override
    public HttpURLConnection getConnectionFromURL(URL url) throws IOException, AuthenticationException, NotAuthenticatedException {
        if (httpManager == null)
            throw new NotAuthenticatedException("Not authenticated");
        return CopernicusHTTPAuthManager.getNewHttpManager(pair.getKey(),pair.getValue()).getConnectionFromURL(url);
    }

    /**
     * Verify is a product is available to download
     * @param id id of the product
     * @return True if product is available to download; false if it is not
     * @throws IOException error reading content from URL
     * @throws AuthenticationException if credentials are wrong
     * @throws NotAuthenticatedException no credentials setted
     */
    @Override
    public boolean isProductOnline(String id) throws IOException, AuthenticationException, NotAuthenticatedException {
        if (httpManager == null)
            throw new NotAuthenticatedException("Not authenticated");
        HttpsURLConnection connection = CopernicusHTTPAuthManager.getNewHttpManager(pair.getKey(), pair.getValue()).getConnectionFromURL(new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('" + id + "')/Online/$value"));
        String s = readFromURL(connection.getInputStream());
        connection.disconnect();
        return s != null && s.equals("true");
    }

    /**
     * Get the file checksum of a product
     * @param id id of the product
     * @return MD5 Checksum of the file product
     * @throws IOException error reading content from URL
     * @throws AuthenticationException if credentials are wrong
     * @throws NotAuthenticatedException no credentials setted
     */
    @Override
    public String getMD5CheckSum(String id) throws IOException, AuthenticationException, NotAuthenticatedException {
        if (httpManager == null)
            throw new NotAuthenticatedException("Not authenticated");
        HttpsURLConnection connection = CopernicusHTTPAuthManager.getNewHttpManager(pair.getKey(), pair.getValue()).getConnectionFromURL(new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('" + id + "')/Checksum/Value/$value"));
        String s = readFromURL(connection.getInputStream());
        connection.disconnect();
        return s;
    }

    /*/**
     * Get the image preview of a product
     * @param id id of product
     * @return image as InputStream
     * @throws IOException error reading image
     * @throws AuthenticationException if credentials are wrong
     * @throws NotAuthenticatedException no credentials setted
     */
    /*public InputStream getPreview(String id) throws IOException, AuthenticationException, NotAuthenticatedException {
        String url = "https://scihub.copernicus.eu/dhus/odata/v1/Products('"+id+"')/Products('Quicklook')/$value";
        return getContentFromURL(new URL(url));
    }*/
}

package services;

import controller.search.CopernicusOpenSearchController;
import gui.dialog.ScihubCredentialsDialog;
import javafx.concurrent.Task;
import javafx.util.Pair;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.CopernicusHTTPAuthManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static utils.AlertFactory.showErrorDialog;

public class CopernicusService {

    static final Logger logger = LogManager.getLogger(CopernicusOpenSearchController.class.getName());
    private static CopernicusService instance;

    private CopernicusHTTPAuthManager httpManager;
    private boolean isConnected;
    private Pair<String, String> pair;

    private CopernicusService() {
        ScihubCredentialsDialog dialog = new ScihubCredentialsDialog();
        Optional<Pair<String, String>> stringStringPair = dialog.showAndWait();
        stringStringPair.ifPresent(stringPair -> this.pair = stringPair);
    }

    public static CopernicusService getInstance() {
        if (instance == null || instance.httpManager == null)
            instance = new CopernicusService();
        return instance;
    }

    public void login() throws AuthenticationException, NotAuthenticatedException {
        if (pair == null) {
            instance = null;
            throw new NotAuthenticatedException("Not authenticated");
        }
        httpManager = CopernicusHTTPAuthManager.getHttpManager(pair.getKey(),pair.getValue());
    }

    public InputStream getContentFromURL(URL url) throws IOException, AuthenticationException, NotAuthenticatedException {
        if (httpManager == null)
            throw new NotAuthenticatedException("Not authentificated");
        return httpManager.getContentFromURL(url);
    }

    public InputStream getPreview(String id) throws IOException, AuthenticationException, NotAuthenticatedException {
        String url = "https://scihub.copernicus.eu/dhus/odata/v1/Products('"+id+"')/Products('Quicklook')/$value";
        return getContentFromURL(new URL(url));
    }

    public boolean isConnected() {
        return isConnected;
    }
}

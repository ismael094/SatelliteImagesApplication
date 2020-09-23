package services;

import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.openSearcher.OpenSearchQueryParameter;
import model.openSearcher.OpenSearchResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.HTTPAuthManager;
import utils.ProductMapper;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

public class OpenSearcher implements SearchService {
    public static final String ALL = "*";
    public static final String AND = " AND ";
    private final String URL = "https://scihub.copernicus.eu/dhus/search?format=json";
    private HTTPAuthManager httpManager;
    private int productsPerPage;
    private int page;
    private final Map<OpenSearchQueryParameter, String> searchParameters;

    @Singleton
    private static OpenSearcher openSearcher;
    static final Logger logger = LogManager.getLogger(OpenSearcher.class.getName());


    public static OpenSearcher getOpenSearcher(String username, String password) throws AuthenticationException {
        if (openSearcher == null)
            openSearcher = new OpenSearcher(username,password);
        openSearcher.login(username,password);
        return openSearcher;

    }

    public OpenSearcher() {
        initData();
        this.searchParameters = new LinkedHashMap<>();
        httpManager = null;
    }

    public OpenSearcher(String username, String password) throws AuthenticationException {
        initData();
        this.searchParameters = new LinkedHashMap<>();
        login(username,password);
    }

    private void initData() {
        this.page = 0;
        this.productsPerPage = 100;
    }

    public int getProductsPerPage() {
        return this.productsPerPage;
    }

    public void setProductPerPage(int productsPerPage) {
        this.productsPerPage = productsPerPage;
    }

    public void login(String username, String password) throws AuthenticationException {
        this.httpManager = HTTPAuthManager.getHttpManager(username,password);
        try {
            String LOGIN_URL = "https://scihub.copernicus.eu/dhus/search?q=*&rows=0&format=json";
            this.httpManager.getContentFromURL(new URL(LOGIN_URL));
        } catch (IOException e) {
            logger.atInfo().log("Not able to connect to SciHub API: {0}",e);
            e.printStackTrace();
        }
    }

    public URL getURL() throws MalformedURLException {
        return new URL(URL + startPage() + rowsPerPage() + searchParameter());
    }

    private String searchParameter() {
        return "&q=("+ getSearchParametersAsString()+")";
    }

    public String getSearchParametersAsString() {
        if (this.searchParameters.size() == 0)
            return ALL;
        return this.searchParameters.entrySet().stream()
                .map((entry)->joinParameterNameValue(entry.getKey(),entry.getValue()))
                .collect(Collectors.joining(AND));
    }

    private String rowsPerPage() {
        return "&rows=" + getProductsPerPage();
    }

    private String startPage() {
        return "&start=" + getStartProductIndex();
    }

    public void setStartProductIndex(int page) {
        this.page = page;
    }

    public int getStartProductIndex() {
        return page;
    }

    public void addSearchParameter(OpenSearchQueryParameter parameterName, String value) {
        this.searchParameters.put(parameterName, value);
    }

    private String joinParameterNameValue(OpenSearchQueryParameter parameterName, String value) {
        return parameterName.getParameterName()+":"+value;
    }

    public Map<OpenSearchQueryParameter,String> getSearchParameters() {
        return this.searchParameters;
    }

    public void clearSearchParameters() {
        this.searchParameters.clear();
    }

    @Override
    public OpenSearchResponse search() throws IOException, AuthenticationException, NotAuthenticatedException {
        if (httpManager == null) {
            logger.atWarn().log("Not authenticated in Copernicus OpenSearch");
            throw new NotAuthenticatedException("Not authenticated in OpenSearch");
        }

        long start = currentTimeMillis();
        OpenSearchResponse response = ProductMapper.getResponse(httpManager.getContentFromURL(getURL()));
        long finish = currentTimeMillis() - start;
        logger.atInfo().log("Product loaded in {} seconds",finish/1000.0);
        return response;
    }
}

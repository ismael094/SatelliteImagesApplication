package services.search;

import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.openSearcher.OpenSearchQueryParameter;
import model.openSearcher.OpenSearchResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.CopernicusService;
import utils.CopernicusHTTPAuthManager;
import utils.ProductMapper;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
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
    private CopernicusService service;
    private int productsPerPage;
    private int page;
    private Map<OpenSearchQueryParameter, String> searchParameters;
    static final Logger logger = LogManager.getLogger(OpenSearcher.class.getName());

    public OpenSearcher() {
        initData();
        this.service = CopernicusService.getInstance();
    }

    public void login() throws AuthenticationException, NotAuthenticatedException {
        service.login();
    }

    private void initData() {
        this.page = 0;
        this.productsPerPage = 100;
        this.searchParameters = new LinkedHashMap<>();
    }

    public void setProductPerPage(int productsPerPage) {
        this.productsPerPage = productsPerPage;
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
        if (service == null) {
            logger.atWarn().log("Not authenticated in Copernicus OpenSearch");
            throw new NotAuthenticatedException("Not authenticated in OpenSearch");
        }
        long start = currentTimeMillis();
        InputStream contentFromURL = service.getContentFromURL(getURL());
        OpenSearchResponse response = ProductMapper.getResponse(contentFromURL);
        contentFromURL.close();
        long finish = currentTimeMillis() - start;
        logger.atInfo().log("{} products loaded in {} seconds",response.getProducts().size(),finish/1000.0);
        return response;
    }

    public int getProductsPerPage() {
        return this.productsPerPage;
    }
}

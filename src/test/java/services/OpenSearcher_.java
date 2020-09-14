package services;

import model.OpenSearchParameter;
import model.exception.AuthenticationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import utils.HTTPAuthManager;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OpenSearcher_ {
    public final String URL = "https://scihub.copernicus.eu/dhus/search?format=json&";
    public static final String VALID_USERNAME = "user";
    public static final String VALID_PASSWORD = "pass";
    public static final String INVALID_USERNAME = "usernameError";
    public static final String INVALID_PASSWORD = "passw11";
    public OpenSearcher openSearcher;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    @Before
    public void initMockup() throws AuthenticationException {
        openSearcher = new OpenSearcher();
        //openSearcher = mock(OpenSearcher.class);
        /*doReturn(CONTENT_LENGTH).when(product).getContentLength();
        doReturn(ID).when(product).getId();
        doReturn(NAME).when(product).getName();
        doReturn(CREATION_TIME).when(product).getCreationDate();*/

    }

    @Test()
    public void with_invalid_credentials_should_throw_exception() throws AuthenticationException {
        OpenSearcher openSearcher = new OpenSearcher(INVALID_USERNAME, VALID_PASSWORD);

    }

    @Test
    public void one_product_per_page_parameter() {
        assertThat(openSearcher.getProductsPerPage()).isEqualTo(100);
        openSearcher.setProductPerPage(1);
        assertThat(openSearcher.getProductsPerPage()).isEqualTo(1);
        openSearcher.setProductPerPage(5);
        assertThat(openSearcher.getProductsPerPage()).isEqualTo(5);
    }

    @Test
    public void set_page() {
        assertThat(openSearcher.getPage()).isEqualTo(0);
        openSearcher.setPage(1);
        assertThat(openSearcher.getPage()).isEqualTo(1);
        openSearcher.setPage(5);
        assertThat(openSearcher.getPage()).isEqualTo(5);
    }

    @Test
    public void with_platform_name_sentinel1_should_return_query_in_path() {
        openSearcher.addSearchParameter(OpenSearchParameter.PLATFORM_NAME,"Sentinel-1");
        assertThat(openSearcher.getSearchParametersAsString()).isEqualTo("platformname:Sentinel-1");
    }

    @Test
    public void with_multiple_search_parameter_should_return_path_with_all_parameter_with_AND_between_then() throws MalformedURLException {
        openSearcher.addSearchParameter(OpenSearchParameter.PLATFORM_NAME,"Sentinel-1");
        openSearcher.addSearchParameter(OpenSearchParameter.PRODUCT_TYPE,"SLC");
        assertThat(openSearcher.getSearchParameters().size()).isEqualTo(2);
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=0&rows=100&q=(platformname:Sentinel-1 AND producttype:SLC)");
    }

    @Test
    public void multiple_parameters_with_same_name_and_value_should_not_be_stored() throws MalformedURLException {
        openSearcher.addSearchParameter(OpenSearchParameter.PLATFORM_NAME,"Sentinel-1");
        openSearcher.addSearchParameter(OpenSearchParameter.PLATFORM_NAME,"Sentinel-1");
        assertThat(openSearcher.getSearchParameters().size()).isEqualTo(1);
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=0&rows=100&q=(platformname:Sentinel-1)");
    }

    @Test
    public void clear_search_parameters_should_delete_all_parameters_stored() throws MalformedURLException {
        openSearcher.addSearchParameter(OpenSearchParameter.PLATFORM_NAME,"Sentinel-1");
        openSearcher.addSearchParameter(OpenSearchParameter.PRODUCT_TYPE,"SLC");
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=0&rows=100&q=(platformname:Sentinel-1 AND producttype:SLC)");
        openSearcher.clearSearchParameters();
        assertThat(openSearcher.getSearchParameters().size()).isEqualTo(0);
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=0&rows=100&q=(*)");
    }

    @Test
    public void get_URL_of_query_with_setted_product_per_page_and_number_page() throws MalformedURLException {
        openSearcher.setProductPerPage(1);
        openSearcher.setPage(1);
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=1&rows=1&q=(*)");
        openSearcher.setProductPerPage(100);
        openSearcher.setPage(0);
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=0&rows=100&q=(*)");
    }

    @Test
    public void get_URL_of_query_with_one_search_parameter() throws MalformedURLException {
        openSearcher.addSearchParameter(OpenSearchParameter.PLATFORM_NAME,"Sentinel-1");
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=0&rows=100&q=(platformname:Sentinel-1)");
        openSearcher.clearSearchParameters();
        openSearcher.addSearchParameter(OpenSearchParameter.PRODUCT_TYPE,"SLC");
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=0&rows=100&q=(producttype:SLC)");
    }

    @Test
    public void search_with() throws MalformedURLException {
        openSearcher.addSearchParameter(OpenSearchParameter.PLATFORM_NAME,"Sentinel-1");
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=0&rows=100&q=(platformname:Sentinel-1)");
        openSearcher.clearSearchParameters();
        openSearcher.addSearchParameter(OpenSearchParameter.PRODUCT_TYPE,"SLC");
        assertThat(openSearcher.getURl().toString()).isEqualTo(URL+"start=0&rows=100&q=(producttype:SLC)");
    }
}

class OpenSearcher {
    public static final String ALL = "*";
    public static final String AND = " AND ";
    private final String URL = "https://scihub.copernicus.eu/dhus/search?format=json";
    private HTTPAuthManager httpManager;
    private int productsPerPage;
    private int page;
    private final Map<OpenSearchParameter, String> searchParameters;

    private static OpenSearcher openSearcher;

    public static OpenSearcher getOpenSearcher(String username, String password) throws AuthenticationException {
        if (openSearcher == null)
            openSearcher = new OpenSearcher(username,password);
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

    private void login(String username, String password) throws AuthenticationException {
        long startTime = currentTimeMillis();
        this.httpManager = new HTTPAuthManager(username,password);
        try {
            String LOGIN_URL = "https://scihub.copernicus.eu/dhus/search?q=*&rows=0";
            this.httpManager.getContentFromURL(new URL(LOGIN_URL));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public URL getURl() throws MalformedURLException {
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
        return "&start=" + getPage();
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void addSearchParameter(OpenSearchParameter parameterName, String value) {
        this.searchParameters.put(parameterName, value);
    }

    private String joinParameterNameValue(OpenSearchParameter parameterName, String value) {
        return parameterName.getParameterName()+":"+value;
    }

    public Map<OpenSearchParameter,String> getSearchParameters() {
        return this.searchParameters;
    }

    public void clearSearchParameters() {
        this.searchParameters.clear();
    }
}

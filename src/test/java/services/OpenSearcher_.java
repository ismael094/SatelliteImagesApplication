package services;

import model.exception.NotAuthenticatedException;
import model.openSearcher.OpenSearchQueryParameter;
import model.exception.AuthenticationException;
import model.openSearcher.OpenSearchResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import utils.HTTPAuthManager;
import utils.ProductMapper;

import java.io.IOException;
import java.io.InputStream;
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
    public OpenSearcher openSearcherMock;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    @Before
    public void initMockup() throws AuthenticationException {
        openSearcher = new OpenSearcher();
        //openSearcherMock = mock(OpenSearcher.class);
        /*doReturn(CONTENT_LENGTH).when(product).getContentLength();
        doReturn(ID).when(product).getId();
        doReturn(NAME).when(product).getName();
        doReturn(CREATION_TIME).when(product).getCreationDate();*/

    }

    @Test(expected = AuthenticationException.class)
    public void with_invalid_credentials_should_throw_exception() throws AuthenticationException {
        new OpenSearcher(INVALID_USERNAME, VALID_PASSWORD);

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
        openSearcher.addSearchParameter(OpenSearchQueryParameter.PLATFORM_NAME,"Sentinel-1");
        assertThat(openSearcher.getSearchParametersAsString()).isEqualTo("platformname:Sentinel-1");
    }

    @Test
    public void with_multiple_search_parameter_should_return_path_with_all_parameter_with_AND_between_then() throws MalformedURLException {
        openSearcher.addSearchParameter(OpenSearchQueryParameter.PLATFORM_NAME,"Sentinel-1");
        openSearcher.addSearchParameter(OpenSearchQueryParameter.PRODUCT_TYPE,"SLC");
        assertThat(openSearcher.getSearchParameters().size()).isEqualTo(2);
        assertThat(openSearcher.getURL().toString()).isEqualTo(URL+"start=0&rows=100&q=(platformname:Sentinel-1 AND producttype:SLC)");
    }

    @Test
    public void multiple_parameters_with_same_name_and_value_should_not_be_stored() throws MalformedURLException {
        openSearcher.addSearchParameter(OpenSearchQueryParameter.PLATFORM_NAME,"Sentinel-1");
        openSearcher.addSearchParameter(OpenSearchQueryParameter.PLATFORM_NAME,"Sentinel-1");
        assertThat(openSearcher.getSearchParameters().size()).isEqualTo(1);
        assertThat(openSearcher.getURL().toString()).isEqualTo(URL+"start=0&rows=100&q=(platformname:Sentinel-1)");
    }

    @Test
    public void clear_search_parameters_should_delete_all_parameters_stored() throws MalformedURLException {
        openSearcher.addSearchParameter(OpenSearchQueryParameter.PLATFORM_NAME,"Sentinel-1");
        openSearcher.addSearchParameter(OpenSearchQueryParameter.PRODUCT_TYPE,"SLC");
        assertThat(openSearcher.getURL().toString()).isEqualTo(URL+"start=0&rows=100&q=(platformname:Sentinel-1 AND producttype:SLC)");
        openSearcher.clearSearchParameters();
        assertThat(openSearcher.getSearchParameters().size()).isEqualTo(0);
        assertThat(openSearcher.getURL().toString()).isEqualTo(URL+"start=0&rows=100&q=(*)");
    }

    @Test
    public void get_URL_of_query_with_setted_product_per_page_and_number_page() throws MalformedURLException {
        openSearcher.setProductPerPage(1);
        openSearcher.setPage(1);
        assertThat(openSearcher.getURL().toString()).isEqualTo(URL+"start=1&rows=1&q=(*)");
        openSearcher.setProductPerPage(100);
        openSearcher.setPage(0);
        assertThat(openSearcher.getURL().toString()).isEqualTo(URL+"start=0&rows=100&q=(*)");
    }

    @Test
    public void get_URL_of_query_with_one_search_parameter() throws MalformedURLException {
        openSearcher.addSearchParameter(OpenSearchQueryParameter.PLATFORM_NAME,"Sentinel-1");
        assertThat(openSearcher.getURL().toString()).isEqualTo(URL+"start=0&rows=100&q=(platformname:Sentinel-1)");
        openSearcher.clearSearchParameters();
        openSearcher.addSearchParameter(OpenSearchQueryParameter.PRODUCT_TYPE,"SLC");
        assertThat(openSearcher.getURL().toString()).isEqualTo(URL+"start=0&rows=100&q=(producttype:SLC)");
    }

    @Test(expected = NotAuthenticatedException.class)
    public void search_with_default_query_should_return_an_open_response() throws IOException, AuthenticationException, NotAuthenticatedException {
        assertThat(openSearcher.search().getNumOfProducts()).isEqualTo(0);
    }
}



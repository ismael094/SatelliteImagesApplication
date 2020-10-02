package services.search;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CopernicusOpenSearcher_ {
    public final String URL = "https://scihub.copernicus.eu/dhus/search?format=json&";
    public static final String VALID_USERNAME = "user";
    public static final String VALID_PASSWORD = "pass";
    public static final String INVALID_USERNAME = "usernameError";
    public static final String INVALID_PASSWORD = "passw11";
    public OpenSearcher openSearcher;
    public OpenSearcher openSearcherMock;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    public void a() {
    }

/*
    @Before
    public void initMockup() throws AuthenticationException {
        HttpManager.
        openSearcher = new OpenSearcher();
        //openSearcherMock = mock(OpenSearcher.class);
        /*doReturn(CONTENT_LENGTH).when(product).getContentLength();
        doReturn(ID).when(product).getId();
        doReturn(NAME).when(product).getName();
        doReturn(CREATION_TIME).when(product).getCreationDate();*/

    //}
    /*

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
        assertThat(openSearcher.getStartProductIndex()).isEqualTo(0);
        openSearcher.setStartProductIndex(1);
        assertThat(openSearcher.getStartProductIndex()).isEqualTo(1);
        openSearcher.setStartProductIndex(5);
        assertThat(openSearcher.getStartProductIndex()).isEqualTo(5);
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
        openSearcher.setStartProductIndex(1);
        assertThat(openSearcher.getURL().toString()).isEqualTo(URL+"start=1&rows=1&q=(*)");
        openSearcher.setProductPerPage(100);
        openSearcher.setStartProductIndex(0);
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
    }*/
}



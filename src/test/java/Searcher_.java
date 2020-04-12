import model.filter.Filter;
import model.filter.filterItems.FilterItemStartWith;
import model.Product;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.junit.Before;
import org.junit.Test;
import services.Searcher;

import java.util.List;

import static org.apache.olingo.client.core.ODataClientFactory.getClient;
import static org.assertj.core.api.Assertions.assertThat;

public class Searcher_ {
    public static final ODataClient client = getClient();
    private final String iCrmServiceRoot = "https://scihub.copernicus.eu/dhus/odata/v1";
    private final String username = "ismael096";
    private final String pass = "Test_password";

    private Searcher searcher;

    @Before
    public void init() {
        searcher = new Searcher();
    }

    @Test
    public void http_authentification_should_return_OK_with_valid_user() {

        ODataRetrieveResponse<ClientEntity> request = searcher.getEntityResponse(getClient().newURIBuilder(iCrmServiceRoot)
                        .appendEntitySetSegment("Products").appendKeySegment("d046d2c8-edc8-4fd1-919e-71ce14b8b1ed")
                        .build());
        assertThat(request.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void should_return_an_image_with_valid_id() {
        Product product = searcher.getImageById("d046d2c8-edc8-4fd1-919e-71ce14b8b1ed");
        assertThat(product.getId()).isEqualTo("d046d2c8-edc8-4fd1-919e-71ce14b8b1ed");
    }

    @Test
    public void should_a_collection_of_image_with_name_S1() {
        Filter filter = new Filter();
        filter.add(new FilterItemStartWith("Name","S1"));
        List<Product> products = searcher.getImages(filter);
        assertThat(products.get(0).getName()).contains("S1");
    }

    @Test
    public void get_attributes_of_image() {
        ClientEntitySet imageAttributesById = searcher.getImageAttributesById("06ffb973-2be6-4ace-b813-5d6e24792af2");
        imageAttributesById.getEntities();
        for (ClientEntity c : imageAttributesById.getEntities()) {
            System.out.println(c.getProperties().get(0));
        }
    }


}

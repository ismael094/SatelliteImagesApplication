package services.search;

import model.filter.Filter;
import model.filter.filterItems.FilterItemStartWith;
import model.ProductOData;
import org.junit.Before;
import org.junit.Test;
import services.search.ODataSearcher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CopernicusODataSearch_ {
    private final String iCrmServiceRoot = "https://scihub.copernicus.eu/dhus/odata/v1";
    private final String username = "ismael096";
    private final String pass = "Test_password";

    private ODataSearcher ODataSearcher;

    @Before
    public void init() {
        ODataSearcher = new ODataSearcher();
    }

   /* @Test
    public void http_authentification_should_return_OK_with_valid_user() {

        ODataRetrieveResponse<ClientEntity> request = ODataSearcher.getEntityResponse(getClient().newURIBuilder(iCrmServiceRoot)
                        .appendEntitySetSegment("Products").appendKeySegment("d046d2c8-edc8-4fd1-919e-71ce14b8b1ed")
                        .build());
        assertThat(request.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void should_return_an_image_with_valid_id() {
        ProductOData productOData = ODataSearcher.getImageById("d046d2c8-edc8-4fd1-919e-71ce14b8b1ed");
        assertThat(productOData.getId()).isEqualTo("d046d2c8-edc8-4fd1-919e-71ce14b8b1ed");
    }

    @Test
    public void should_a_collection_of_image_with_name_S1() {
        Filter filter = new Filter();
        filter.add(new FilterItemStartWith("Name","S1"));
        List<ProductOData> productOData = ODataSearcher.getImages(filter);
        assertThat(productOData.get(0).getName()).contains("S1");
    }

    @Test
    public void get_attributes_of_image() {
        ClientEntitySet imageAttributesById = ODataSearcher.getImageAttributesById("06ffb973-2be6-4ace-b813-5d6e24792af2");
        imageAttributesById.getEntities();
        for (ClientEntity c : imageAttributesById.getEntities()) {
            System.out.println(c.getProperties().get(0));
        }
    }*/
}

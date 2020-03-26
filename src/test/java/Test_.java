

import org.apache.commons.codec.binary.Base64;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.olingo.client.core.ODataClientFactory.getClient;

public class Test_ {

    public static final ODataClient client = getClient();

    @Test
    public void testing() {

        client.getConfiguration().setDefaultPubFormat(ContentType.APPLICATION_JSON);
        client.getConfiguration()
                .setHttpClientFactory(new BasicAuthHttpClientFactory("ismael096", "Test_password"));

        String iCrmServiceRoot = "https://scihub.copernicus.eu/dhus/odata/v2";
        //ODataServiceDocumentRequest odClientReq = client.getRetrieveRequestFactory().getServiceDocumentRequest(iCrmServiceRoot);
        ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory()
                .getEntityRequest(getClient().newURIBuilder(iCrmServiceRoot)
                        .appendEntitySetSegment("Products").appendKeySegment("d046d2c8-edc8-4fd1-919e-71ce14b8b1ed")
                        .build());
        ;
        System.out.println(request.getURI().toString());
        final ODataRetrieveResponse<ClientEntity> response = request.execute();
        final ClientEntity entitySet = response.getBody();
        System.out.println(entitySet.getProperties().get(0).getValue());
    }

    @Test
    public void testing2() {
        /*Testing a = new Testing();
        try {
            a.readTickets();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void testing3() {
        /*String serviceUrl = "https://scihub.copernicus.eu/dhus/odata/v1/Products";
        ODataConsumer consumer = ODataConsumers.create(serviceUrl);
        OClientBehavior basicAuth = OClientBehaviors.basicAuth("ismael096", "Test_password");
        ODataConsumer.Builder builder = ODataConsumers.newBuilder("https://scihub.copernicus.eu/dhus/odata/v1").setClientBehaviors(basicAuth);

        ODataConsumer build = builder.build();
        EdmDataServices metadata = build.getMetadata();

        //OFunctionRequest<OObject> oObjectOFunctionRequest = build.callFunction("Products").pString("Id", "d046d2c8-edc8-4fd1-919e-71ce14b8b1ed");
        //System.out.println(oObjectOFunctionRequest.execute().count());
        OQueryRequest<OEntity> products = build.getEntities("Attributes");

        Enumerable<OEntity> execute = products.filter("Id eq 'd046d2c8-edc8-4fd1-919e-71ce14b8b1ed'").execute();
        // list category names
        for (OEntity category : execute) {
            List<OProperty<?>> properties = category.getProperties();
            for (OProperty<?> property :
                    properties) {
                EdmType type = property.getType();

                System.out.println(type.toString());
                System.out.println(property.getName() + " - " + property.getValue());
            }
        }*/


    }
}

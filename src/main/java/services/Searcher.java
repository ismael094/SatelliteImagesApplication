package services;

import model.filter.Filter;
import model.Product;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ContentType;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.apache.olingo.client.core.ODataClientFactory.getClient;

public class Searcher {
    public static final ODataClient client = getClient();
    private final String iCrmServiceRoot = "https://scihub.copernicus.eu/dhus/odata/v2";
    private final String username = "ismael096";
    private final String pass = "Test_password";

    public Searcher() {
        setAuthentificationCredential(username,pass);
    }

    public void setAuthentificationCredential(String username, String pass) {
        client.getConfiguration().setDefaultPubFormat(ContentType.APPLICATION_JSON);
        client.getConfiguration()
                .setHttpClientFactory(new BasicAuthHttpClientFactory(username, pass));
    }

    public ODataRetrieveResponse<ClientEntity> getEntityResponse(URI uri) {
        System.out.println(uri.toString());
        return client.getRetrieveRequestFactory()
                .getEntityRequest(uri).execute();
    }

    public ODataEntitySetRequest<ClientEntitySet> getEntitySetRequest(URI uri) {
        System.out.println(uri.toString());
        return client.getRetrieveRequestFactory()
                .getEntitySetRequest(uri);
    }

    public ODataRetrieveResponse<ClientEntitySet> getEntitySetResponse(ODataEntitySetRequest<ClientEntitySet> request) {

        return request.execute();
    }

    public Product getImageById(String id) {
        ODataRetrieveResponse<ClientEntity> response = getEntityResponse(getClient().newURIBuilder(iCrmServiceRoot)
                .appendEntitySetSegment("Products").appendKeySegment(id)
                .build());
        return parseProduct(response.getBody());
    }

    public ClientEntitySet getImageAttributesById(String id) {

        ODataRetrieveResponse<ClientEntitySet> response = getEntitySetResponse(getEntitySetRequest(getClient().newURIBuilder(iCrmServiceRoot)
                .appendEntitySetSegment("Products").appendKeySegment(id).appendEntitySetSegment("Attributes")
                .build()));
        return response.getBody();
    }

    public List<Product> getImages(Filter filter) {
        ODataRetrieveResponse<ClientEntitySet> response = getEntitySetResponse(getEntitySetRequest(getClient().newURIBuilder(iCrmServiceRoot)
                .appendEntitySetSegment("Products").filter(filter.evaluate()).orderBy("IngestionDate desc")
                .build()));
        return parseProducts(response.getBody());
    }

    private List<Product> parseProducts(ClientEntitySet entitySet) {
        entitySet.getEntities();
        List<Product> list = new ArrayList<>();
        for (ClientEntity entity : entitySet.getEntities())
            list.add(parseProduct(entity));
        return list;
    }

    private Product parseProduct(ClientEntity entity) {
        Product product = new Product();
        for (ClientProperty property : entity.getProperties()) {
            try {
                if (product.getClass().getDeclaredField(property.getName()) == null)
                    continue;
                if (getPropertyType(property).equals("String"))
                    product.setField(property.getName(),property.getValue().toString());
                else if (getPropertyType(property).equals("Int32"))
                    product.setField(property.getName(),property.getValue().asPrimitive().toCastValue(Integer.class));
                else if (getPropertyType(property).equals("Int64")) {
                    product.setField(property.getName(),property.getValue().asPrimitive().toCastValue(Long.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return product;
    }

    public String getPropertyType(ClientProperty property) {
        return  property.getValue().getTypeName() != null ? property.getValue().getTypeName().substring(4) : "";
    }

    public void getImageAttributesdById(String id) {
        ODataEntitySetRequest<ClientEntitySet> request = client.getRetrieveRequestFactory()
                .getEntitySetRequest(getClient().newURIBuilder(iCrmServiceRoot)
                        .appendEntitySetSegment("Products").appendKeySegment(id)
                        .appendEntitySetSegment("Attributes").build());
        System.out.println(request.getURI().toString());
        final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
        final ClientEntitySet entitySet = response.getBody();
        System.out.println(entitySet.getEntities().get(0).toString());
    }
}

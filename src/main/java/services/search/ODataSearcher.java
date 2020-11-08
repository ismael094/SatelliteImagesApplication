package services.search;

import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.filter.Filter;
import model.ProductOData;
import org.apache.olingo.commons.api.format.ContentType;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class ODataSearcher {
    /*ublic static final ODataClient client = getClient();
    private final String iCrmServiceRoot = "https://scihub.copernicus.eu/dhus/odata/v2";


    public ODataSearcher() {
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

    public ProductOData getImageById(String id) {
        ODataRetrieveResponse<ClientEntity> response = getEntityResponse(getClient().newURIBuilder(iCrmServiceRoot)
                .appendEntitySetSegment("Products").appendKeySegment(id)
                .build());
        return parseProduct(response.getBody());
    }

    public ProductOData testImage(String id) {
        ODataRetrieveResponse<ClientEntity> response = getEntityResponse(getClient().newURIBuilder(iCrmServiceRoot)
                .appendEntitySetSegment("Products").appendKeySegment(id)
                .appendEntitySetSegment("$value")
                .build());

        return parseProduct(response.getBody());
    }

    public ClientEntitySet getImageAttributesById(String id) {

        ODataRetrieveResponse<ClientEntitySet> response = getEntitySetResponse(getEntitySetRequest(getClient().newURIBuilder(iCrmServiceRoot)
                .appendEntitySetSegment("Products").appendKeySegment(id).appendEntitySetSegment("Attributes")
                .build()));
        return response.getBody();
    }

    public List<ProductOData> getImages(Filter filter) {
        ODataRetrieveResponse<ClientEntitySet> response = getEntitySetResponse(getEntitySetRequest(getClient().newURIBuilder(iCrmServiceRoot)
                .appendEntitySetSegment("Products").filter(filter.evaluate()).orderBy("IngestionDate desc")
                .build()));
        return parseProducts(response.getBody());
    }

    private List<ProductOData> parseProducts(ClientEntitySet entitySet) {
        List<ClientEntity> entities = entitySet.getEntities();
        List<ProductOData> list = new ArrayList<>();
        for (ClientEntity entity : entitySet.getEntities())
            list.add(parseProduct(entity));
        return list;
    }

    private ProductOData parseProduct(ClientEntity entity) {
        ProductOData productOData = new ProductOData();
        for (ClientProperty property : entity.getProperties()) {
            try {
                if (productOData.getClass().getDeclaredField(property.getName()) == null)
                    continue;
                if (getPropertyType(property).equals("String"))
                    productOData.setField(property.getName(),property.getValue().toString());
                else if (getPropertyType(property).equals("Int32"))
                    productOData.setField(property.getName(),property.getValue().asPrimitive().toCastValue(Integer.class));
                else if (getPropertyType(property).equals("Int64")) {
                    productOData.setField(property.getName(),property.getValue().asPrimitive().toCastValue(Long.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return productOData;
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

    @Override
    public Object search() throws IOException, AuthenticationException, NotAuthenticatedException {
        return null;
    }*/
}

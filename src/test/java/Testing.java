

import java.io.*;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;


import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;


public class Testing {

    /*public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_PATCH = "PATCH";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HTTP_HEADER_ACCEPT = "Accept";

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";
    public static final String METADATA = "$metadata";
    public static final String SEPARATOR = "/";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String CSRF_TOKEN_HEADER = "X-CSRF-Token";
    public static final String CSRF_TOKEN_FETCH = "Fetch";
    public static final String C4C_TENANT = "C4C_TENANT";

    private static final Logger logger = Logger
                .getLogger(Testing.class.getName());

    private String boundary = "batch_" + UUID.randomUUID().toString();

    private HttpClient m_httpClient = null;
    private Edm m_edm = null;
    private String m_csrfToken = null;

    private Edm readEdm() throws EntityProviderException,
            IllegalStateException, IOException {

        // This is used for both setting the Edm and CSRF Token :)
        if (m_edm != null) {
            return m_edm;
        }

        String serviceUrl = new StringBuilder(getODataServiceUrl())
                .append(SEPARATOR).append(METADATA).toString();

        logger.info("Metadata url => " + serviceUrl);

        final HttpGet get = new HttpGet(serviceUrl);
        get.setHeader(HttpHeaders.AUTHORIZATION, getAuthorizationHeader());
        get.setHeader(CSRF_TOKEN_HEADER, CSRF_TOKEN_FETCH);

        //HttpResponse response = getHttpClient().execute(get);

        HttpResponse response = getHttpClient().execute(get);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            System.out.println("dsklfnsdf");
        }

        InputStream content = response.getEntity().getContent();
        BufferedReader in = new BufferedReader(new InputStreamReader(content));
        String line = null;
        String total = "";
        while((line = in.readLine()) != null) {
            total += line;
        }
        total = total.substring(136, total.length()-32);
        System.out.println(total);
        InputStream targetStream = new ByteArrayInputStream(total.getBytes());

        //logger.info("CSRF token => " + m_csrfToken);
        m_edm = EntityProvider.readMetadata(targetStream,false);
        return m_edm;
    }

    private InputStream executeGet(String absoluteUrl, String contentType)
            throws IllegalStateException, IOException {
        final HttpGet get = new HttpGet(absoluteUrl);
        get.setHeader(AUTHORIZATION_HEADER, getAuthorizationHeader());
        get.setHeader(HTTP_HEADER_ACCEPT, contentType);

        HttpResponse response = getHttpClient().execute(get);
        return response.getEntity().getContent();
    }

    public ODataFeed readFeed(String serviceUri, String contentType,
                              String entitySetName, SystemQueryOptions options)
            throws IllegalStateException, IOException, EntityProviderException,
            EdmException {
        Edm edm = readEdm();
        EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
        String absolutUri = createUri(serviceUri, entitySetName, null, options);

        InputStream content = executeGet(absolutUri, contentType);
        return EntityProvider.readFeed(contentType,
                entityContainer.getEntitySet(entitySetName), content,
                EntityProviderReadProperties.init().build());
    }

    public ODataEntry readEntry(String serviceUri, String contentType,
                                String entitySetName, String keyValue, SystemQueryOptions options)
            throws IllegalStateException, IOException, EdmException,
            EntityProviderException {
        EdmEntityContainer entityContainer = readEdm()
                .getDefaultEntityContainer();
        logger.info("Entity container is => " + entityContainer.getName());
        String absolutUri = createUri(serviceUri, entitySetName, keyValue,
                options);

        InputStream content = executeGet(absolutUri, contentType);

        return EntityProvider.readEntry(contentType,
                entityContainer.getEntitySet(entitySetName), content,
                EntityProviderReadProperties.init().build());
    }

    public String getCsrfToken() {

        if (m_csrfToken != null) {
            return m_csrfToken;
        }

        // Force a server call to fetch the EDM again
        m_edm = null;
        try {
            readEdm();
        } catch (EntityProviderException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

        return m_csrfToken;
    }

    private HttpResponse executeBatchCall(String serviceUrl, final String body)
            throws ClientProtocolException, IOException {
        final HttpPost post = new HttpPost(URI.create(serviceUrl + "/$batch"));
        post.setHeader("Content-Type", "multipart/mixed;boundary=" + boundary);
        post.setHeader(AUTHORIZATION_HEADER, getAuthorizationHeader());
        post.setHeader(CSRF_TOKEN_HEADER, getCsrfToken());
        HttpEntity entity = new StringEntity(body);

        post.setEntity(entity);

        for (Header h : post.getAllHeaders()) {
            logger.info(h.getName() + " : " + h.getValue());
        }

        HttpResponse response = getHttpClient().execute(post);

        logger.info("Response statusCode => "
                + response.getStatusLine().getStatusCode());

        return response;
    }

    private HttpClient getHttpClient() {
        if (this.m_httpClient == null) {
            this.m_httpClient = HttpClientBuilder.create().build();
        }
        return this.m_httpClient;
    }

    public void readTickets() throws Exception {
        String serviceUrl = getODataServiceUrl();

        SystemQueryOptions queryOptions = this.new SystemQueryOptions();
        String queryString = null;
        queryString = "Products('d046d2c8-edc8-4fd1-919e-71ce14b8b1ed')";

        queryOptions.setQueryCondition(queryString);

        ODataFeed feed = readFeed(serviceUrl, APPLICATION_JSON,
                "Products", queryOptions);

        logger.info("Read: " + feed.getEntries().size() + " entries");
    }

    private String createUri(String serviceUri, String entitySetName,
                             String id, SystemQueryOptions options) {

        final StringBuilder absolauteUri = new StringBuilder(serviceUri)
                .append(SEPARATOR).append(entitySetName);
        if (id != null) {
            absolauteUri.append("('").append(id).append("')");
        }

        if (options != null) {
            if (options.getQueryCondition() != null) {
                absolauteUri.append(options.getQueryCondition());
            }
        }

        logger.info("createUri : " + absolauteUri.toString());
        return absolauteUri.toString();
    }

    public class SystemQueryOptions {
        private String queryCondition;

        public String getQueryCondition() {
            return queryCondition;
        }

        public void setQueryCondition(String queryCondition) {
            this.queryCondition = queryCondition;
        }
    }

    private String getODataServiceUrl() {
        return "https://scihub.copernicus.eu/dhus/odata/v2";
    }

    private String getAuthorizationHeader() {
        // Note: This example uses Basic Authentication
        // Preferred option is to use OAuth SAML bearer flow.
        String temp = new StringBuilder("ismael096").append(":")
                .append("Test_password").toString();
        String result = "Basic "
                + new String(Base64.encodeBase64(temp.getBytes()));
        logger.info("AuthorizationHeader " + result);
        return result;
    }*/

}
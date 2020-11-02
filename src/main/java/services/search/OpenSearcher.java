package services.search;

import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.openSearcher.SentinelProductParameters;
import model.openSearcher.OpenSearchResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.CopernicusService;
import utils.ProductDeserializerFactory;
import utils.ServiceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

public class OpenSearcher implements SearchService {
    public static final String ALL = "*";
    public static final String AND = " AND ";
    public static final String ALL_FROM = "*";
    public static final String TO_NOW = "NOW";
    private final String URL = "https://scihub.copernicus.eu/dhus/search?format=json";
    private CopernicusService service;
    private int productsPerPage;
    private int page;
    private Map<SentinelProductParameters, String> searchParameters;
    static final Logger logger = LogManager.getLogger(OpenSearcher.class.getName());

    public OpenSearcher() {
        initData();
    }

    public OpenSearcher(CopernicusService service) {
        this();
        this.service = service;
    }

    /**
     * Login in CopernicusService
     * @throws AuthenticationException If credentials are incorrect
     * @throws NotAuthenticatedException Not credentials setted
     */
    public void login() throws AuthenticationException, NotAuthenticatedException {
        if (service == null)
            service = (CopernicusService) ServiceFactory.getService("Copernicus");
        //service.login();
    }

    /**
     * Init fields
     */
    private void initData() {
        this.page = 0;
        this.productsPerPage = 25;
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

    public void addSearchParameter(SentinelProductParameters parameterName, String value) {
        this.searchParameters.put(parameterName, value);
    }

    private String joinParameterNameValue(SentinelProductParameters parameterName, String value) {
        return parameterName.getParameterName()+":"+value;
    }

    public Map<SentinelProductParameters,String> getSearchParameters() {
        return this.searchParameters;
    }

    public void setSearchParameters(Map<SentinelProductParameters, String> searchParameters) {
        this.searchParameters = searchParameters;
    }

    public void clearSearchParameters() {
        this.searchParameters.clear();
    }

    /**
     * Search with the parameters products in the API of Copernicus
     * @return OpenSearchResponse with number of products in API and list of products
     * @throws IOException Error while reading inputStream
     * @throws AuthenticationException If credentials are incorrect
     * @throws NotAuthenticatedException Not credentials setted
     */
    @Override
    public OpenSearchResponse search() throws IOException, AuthenticationException, NotAuthenticatedException {
        if (service == null) {
            logger.atWarn().log("Not authenticated in Copernicus OpenSearch");
            throw new NotAuthenticatedException("Not authenticated in OpenSearch");
        }
        long start = currentTimeMillis();
        InputStream contentFromURL = service.getContentFromURL(getURL());
        OpenSearchResponse response = (OpenSearchResponse) ProductDeserializerFactory.get("OpenSearch").deserialize(contentFromURL);
        contentFromURL.close();
        long finish = currentTimeMillis() - start;
        logger.atInfo().log("{} products loaded in {} seconds",response.getProducts().size(),finish/1000.0);
        return response;
    }

    public int getProductsPerPage() {
        return this.productsPerPage;
    }

    public void addDateParameter(SentinelProductParameters dateParameter, LocalDate dateStart, LocalDate dateFinish) {
        if (dateStart != null || dateFinish != null)
            addJoinRangeParameter(dateParameter,getDateFromString(dateStart),getDateToString(dateFinish));
    }

    private String getDateFromString(LocalDate date) {
        return getDateString(date, ALL_FROM,0,0,0,":00.001");
    }

    private String getDateToString(LocalDate date) {
        return getDateString(date, TO_NOW,23,59,59,".999");
    }

    private String getDateString(LocalDate date, String dateNull, int hour, int minute, int seconds, String nano) {
        if (date == null)
            return dateNull;

        LocalDateTime localDateTimeFinish = date.atTime(hour, minute, seconds, 0);
        localDateTimeFinish.atZone(ZoneId.of("UTC"));
        return localDateTimeFinish.toString()+nano+"Z";
    }

    public void addJoinRangeParameter(SentinelProductParameters parameter, String from, String to) {
        addSearchParameter(parameter,"["+ from+ " TO " + to + "]");
    }
}

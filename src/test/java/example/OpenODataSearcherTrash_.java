package example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;


public class OpenODataSearcherTrash_ {
    private final String root = "https://scihub.copernicus.eu/dhus/search?start=0&rows=100&q=*&format=json";
    private final String username = "ismael096";
    private final String password = "Test_password";
    HttpAuthenticationFeature feature = HttpAuthenticationFeature.universalBuilder()
            .credentialsForBasic("ismael096", "Test_password")
            .credentials("ismael096", "Test_password").build();

    final Client client = ClientBuilder.newClient();

    @Test
    public void test() {
        client.register(feature);

        URI uri = client
                .target(root)
                .queryParam("q", "*")
                .getUri();
        System.out.println(uri.toString());

        Response request = client
                .target(root)
                .queryParam("q", "*")
                .request(MediaType.APPLICATION_ATOM_XML)
                .get();
        Feed feedType = request.readEntity(Feed.class);
        System.out.println(feedType);
        /*JAXBContext jc = null;
        Unmarshaller unmarshaller = null;
        try {
            jc = JAXBContext.newInstance(FeedType.class);
            unmarshaller = jc.createUnmarshaller();
            FeedType feed = (FeedType) unmarshaller.unmarshal(request.);

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //for (String s : request.getAllowedMethods())
        //System.out.println(s);
        //System.out.println(request.getStatus());
    }

    @Test
    public void read_xml() {
        try {
            String userPassword = username + ":" + password;
            URL url = new URL(root);
            URLConnection urlConnection = url.openConnection();
            String userpass = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.connect();

            JAXBContext jc = JAXBContext.newInstance(Feed.class);
            Unmarshaller u = jc.createUnmarshaller();
            InputStream inputStream = urlConnection.getInputStream();
            Feed feed = (Feed) u.unmarshal(inputStream);
            inputStream.close();

            Product entry = feed.getEntry().get(0);
            /*for (StrType s : entry.getStringProperties())
                System.out.println(s.getName() + " : " + s.getData());*/

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read_json() {
        try {
            String userPassword = username + ":" + password;
            URL url = new URL(root);
            URLConnection urlConnection = url.openConnection();
            String userpass = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.connect();

            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = urlConnection.getInputStream();
            JsonNode jsonNode = mapper.readTree(inputStream);
            JsonNode feed1 = jsonNode.path("feed");
            long startTime = currentTimeMillis();
            Result feed = mapper.treeToValue(feed1, Result.class);
            long finishTime = currentTimeMillis()-startTime;
            List<Entry> entry = Arrays.asList(feed.getEntry());
            System.out.println("Time to parse JSON to POJO: " + finishTime/1000.0+"seg");
            System.out.println("Number of products: " + entry.size());

            entry
            .forEach(System.out::println);
            inputStream.close();

            /*URL url = new URL(root);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String userpass = "ismael096:Test_padssword";
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.connect();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

class EntryDeserializer extends StdDeserializer<Entry> {

    private static final ObjectMapper mapper = new ObjectMapper();
    TypeReference<List<ObjectProperties>> typeRef = new TypeReference<>() {};
    TypeReference<List<ObjectProperties>> typeRefD = new TypeReference<>() {};

    public EntryDeserializer() {
        this(null);
    }

    public EntryDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Entry deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);
        List<ObjectProperties> properties = mapper.convertValue(node.get("str"), typeRef);
        Entry entry = new Entry();
        if ((getPropertyByName("platformname",properties)).equals("Sentinel-1")) {
            entry = new Sentinel1Product();
            Sentinel1Product a = (Sentinel1Product)entry;
            a.setSensorOperationalMode((String)getPropertyByName("sensoroperationalmode",properties));
            a.setPolarizationMode((String)getPropertyByName("polarisationmode",properties));
        } else if (getPropertyByName("platformname",properties).equals("Sentinel-2")) {
            entry = new Sentinel2Product();
            Sentinel2Product a = (Sentinel2Product)entry;
            if (node.get("double").isArray()) {
                List<ObjectProperties> doubleP = mapper.convertValue(node.get("double"), typeRefD);
                a.setCloudCoverPercentage(Double.parseDouble((String) getPropertyByName("cloudcoverpercentage",doubleP)));
            } else {
                a.setCloudCoverPercentage(node.get("double").get("content").asDouble());
            }

        }
        entry.setTitle(node.get("title").asText());
        entry.setId(node.get("id").asText());
        entry.setFootprint((String)getPropertyByName("footprint",properties));
        entry.setSize((String)getPropertyByName("size",properties));
        entry.setProductType((String)getPropertyByName("productType",properties));
        entry.setPlatformName((String)getPropertyByName("platformName",properties));
        /*for (JsonNode jsonNode : node.get("str")) {
            try {
                entry.getClass().getField(jsonNode.get("name").asText()).set(entry,jsonNode.get("content").asText());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }*/


        return entry;
    }

    public Object getPropertyByName(String property, List<ObjectProperties> properties) {
        return properties.stream()
                .filter(e->e.getName().equals(property))
                .map(ObjectProperties::getContent)
                .findFirst()
                .orElse(null);
    }


}


@JsonIgnoreProperties(ignoreUnknown = true)
class Result {
    private Entry[] entry;
    private String title;
    private String id;

    public Result() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Entry[] getEntry() {
        return entry;
    }

    public void setEntry(Entry[] entry) {
        this.entry = entry;
    }
}

@JsonDeserialize(using = EntryDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
class Entry {

    private Date ingestionDate;
    protected String title;
    protected String id;
    private String footprint;
    private String size;
    private String productType;
    private String platformName;
    private String status;

    public Entry() {
    }

    public Entry(Date ingestionDate, String title, String id, String footprint, String size, String productType, String platformName, String status) {
        this.ingestionDate = ingestionDate;
        this.title = title;
        this.id = id;
        this.footprint = footprint;
        this.size = size;
        this.productType = productType;
        this.platformName = platformName;
        this.status = status;
    }

    public Date getIngestionDate() {
        return ingestionDate;
    }

    public void setIngestionDate(Date ingestionDate) {
        this.ingestionDate = ingestionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFootprint(String footprint) {
        this.footprint = footprint;
    }

    public String getFootprint() {
        return footprint;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "ingestionDate=" + ingestionDate +
                ", title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", footprint='" + footprint + '\'' +
                ", size='" + size + '\'' +
                ", productType='" + productType + '\'' +
                ", platformName='" + platformName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

class Sentinel1Product extends Entry {


    private String sensorOperationalMode;
    private String polarizationMode;

    public Sentinel1Product(Date ingestionDate, String title, String id, String footprint, String size,
                            String productType, String platformName, String status, String sensorOperationalMode,
                            String polarizationMode) {
        super(ingestionDate, title, id, footprint, size, productType, platformName, status);
        this.sensorOperationalMode = sensorOperationalMode;
        this.polarizationMode = polarizationMode;
    }

    public Sentinel1Product() {
    }

    public Sentinel1Product(String sensorOperationalMode, String polarizationMode) {
        this.sensorOperationalMode = sensorOperationalMode;
        this.polarizationMode = polarizationMode;
    }

    public String getSensorOperationalMode() {
        return sensorOperationalMode;
    }

    public void setSensorOperationalMode(String sensorOperationalMode) {
        this.sensorOperationalMode = sensorOperationalMode;
    }

    public String getPolarizationMode() {
        return polarizationMode;
    }

    public void setPolarizationMode(String polarizationMode) {
        this.polarizationMode = polarizationMode;
    }

    @Override
    public String toString() {
        return "Sentinel1Product{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", sensorOperationalMode='" + sensorOperationalMode + '\'' +
                ", polarizationMode='" + polarizationMode + '\'' +
                '}';
    }
}

class Sentinel2Product extends Entry {
    private double cloudCoverPercentage;

    public Sentinel2Product(Date ingestionDate, String title, String id, String footprint, String size,
                            String productType, String platformName, String status, double cloudCoverPercentage) {
        super(ingestionDate, title, id, footprint, size, productType, platformName, status);
        this.cloudCoverPercentage = cloudCoverPercentage;
    }

    public Sentinel2Product() {
    }

    public double getCloudCoverPercentage() {
        return cloudCoverPercentage;
    }

    public void setCloudCoverPercentage(double cloudCoverPercentage) {
        this.cloudCoverPercentage = cloudCoverPercentage;
    }

    @Override
    public String toString() {
        return "Sentinel2Product{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", cloudCoverPercentage=" + cloudCoverPercentage +
                '}';
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ObjectProperties {
    private String name;
    @JsonProperty("content")
    private Object content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}

@XmlRootElement(name = "feed")
@XmlAccessorType(XmlAccessType.FIELD)
class Feed {
    private List<Product> entry;

    protected String id;

    public Feed() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "title")
    protected String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Product> getEntry() {
        return entry;
    }

    public void setEntry(List<Product> entry) {
        this.entry = entry;
    }
}



@XmlType(name = "entry",namespace = "http://www.w3.org/2005/Atom")
@XmlAccessorType(XmlAccessType.FIELD)
class Product {

    @XmlElement(name="str",required = true)
    protected List<StrType> stringProperties;
    @XmlElement(name="int",required = true)
    protected List<IntType> intProperties;
    @XmlElement(name="date",required = true)
    protected List<DateType> dateProperties;

    @XmlElement(name = "title")
    protected String title;
    @XmlElement(name = "id")
    protected String id;

    public Product() {
    }

    public List<IntType> getIntProperties() {
        return intProperties;
    }

    public void setIntProperties(List<IntType> intProperties) {
        this.intProperties = intProperties;
    }

    public List<DateType> getDateProperties() {
        return dateProperties;
    }

    public void setDateProperties(List<DateType> dateProperties) {
        this.dateProperties = dateProperties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<StrType> getStringProperties() {
        return stringProperties;
    }

    public void setStringProperties(List<StrType> stringProperties) {
        this.stringProperties = stringProperties;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFootprint() {
        return stringProperties.stream()
                .filter(e -> e.getName().equals("footprint"))
                .map(StrType::getData)
                .collect(Collectors.joining());
    }

}



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "str")
class StrType {

    @XmlAttribute(name = "name")
    private String name;

    @XmlValue
    private String data;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "int")
class IntType {

    @XmlAttribute(name = "name")
    private String name;

    @XmlValue
    private int data;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "date")
class DateType {

    @XmlAttribute(name = "name")
    private String name;

    @XmlValue
    private Date data;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}

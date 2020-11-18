package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.openSearcher.OpenSearchResponse;
import model.products.ProductDTO;
import model.products.ProductProperties;
import model.products.sentinel.Sentinel1ProductDTO;
import model.products.sentinel.Sentinel2ProductDTO;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.deserializer.sentinel.impl.SentinelOpenSearchProductDeserializer;
import utils.deserializer.sentinel.impl.Sentinel1OpenSearchProductDeserializer;
import utils.deserializer.sentinel.SentinelOpenSearchDeserializerManager;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SentinelOpenSearchProductDeserializerTest {
    private InputStream jsonContent,jsonContentOne;
    private JSONObject str;
    TypeReference<List<ProductProperties>> typeRef = new TypeReference<List<ProductProperties>>() {};

    @Before
    public void getJSONExample() throws FileNotFoundException, JSONException {
        jsonContent = new FileInputStream(Paths.get("src/test/java/utils/feed.json").toFile());
        jsonContentOne = new FileInputStream(Paths.get("src/test/java/utils/json_entry.json").toFile());
        str = new JSONObject();
        str.put("str","[{\"name\":\"ecmwf\",\"content\":\"FORECAST\"},{\"name\":\"filename\",\"content\":\"S3B_SL_1_RBT____20200910T120104_20200910T120404_20200910T135948_0179_043_180_2700_LN2_O_NR_004.SEN3\"},{\"name\":\"gmlfootprint\",\"content\":\"<gml:Polygon srsName=\\\"http://www.opengis.net/gml/srs/epsg.xml#4326\\\" xmlns:gml=\\\"http://www.opengis.net/gml\\\">\\n   <gml:outerBoundaryIs>\\n      <gml:LinearRing>\\n         <gml:coordinates>10.4624,-38.5934 10.3731,-38.1365 10.281,-37.6784 10.1873,-37.2199 10.1055,-36.7705 10.005,-36.309 9.91937,-35.8581 9.81796,-35.4019 9.73141,-34.9466 9.63456,-34.4863 9.53908,-34.0329 9.45101,-33.5808 9.34517,-33.1235 9.25232,-32.6732 9.15274,-32.2158 9.05726,-31.7686 8.95688,-31.3145 8.85547,-30.8593 8.76388,-30.4067 8.66148,-29.9517 8.56088,-29.5066 8.45775,-29.0522 8.3548,-28.6042 8.25088,-28.1503 8.15503,-27.6983 8.04974,-27.2466 7.94329,-26.7944 7.843,-26.3478 7.7325,-25.8989 7.63036,-25.4409 7.59002,-25.2674 10.2025,-24.6435 12.8571,-23.9916 15.5095,-23.3196 18.1591,-22.6247 18.2011,-22.8073 18.3214,-23.2775 18.4299,-23.7488 18.5369,-24.2132 18.6494,-24.6836 18.7602,-25.1518 18.8601,-25.6255 18.9678,-26.0978 19.0735,-26.5661 19.1791,-27.0417 19.2811,-27.5069 19.3846,-27.9842 19.4851,-28.4558 19.5778,-28.9356 19.6774,-29.4122 19.7748,-29.8885 19.8668,-30.359 19.9595,-30.8356 20.0515,-31.316 20.1468,-31.7922 20.2416,-32.2737 20.3233,-32.7466 20.4116,-33.2345 20.4934,-33.7121 20.5885,-34.1915 20.6647,-34.6732 20.7377,-35.1536 20.8238,-35.6408 20.9037,-36.1195 20.9813,-36.6028 18.3408,-37.0848 15.7,-37.5781 13.0592,-38.0836 10.4624,-38.5934</gml:coordinates>\\n      </gml:LinearRing>\\n   </gml:outerBoundaryIs>\\n</gml:Polygon>\"},{\"name\":\"format\",\"content\":\"SAFE\"},{\"name\":\"instrumentshortname\",\"content\":\"SLSTR\"},{\"name\":\"sensoroperationalmode\",\"content\":\"Earth Observation\"},{\"name\":\"instrumentname\",\"content\":\"Sea and Land Surface Temperature Radiometer\"},{\"name\":\"footprint\",\"content\":\"MULTIPOLYGON (((-25.2674 7.59002, -24.6435 10.2025, -23.9916 12.8571, -23.3196 15.5095, -22.6247 18.1591, -22.8073 18.2011, -23.2775 18.3214, -23.7488 18.4299, -24.2132 18.5369, -24.6836 18.6494, -25.1518 18.7602, -25.6255 18.8601, -26.0978 18.9678, -26.5661 19.0735, -27.0417 19.1791, -27.5069 19.2811, -27.9842 19.3846, -28.4558 19.4851, -28.9356 19.5778, -29.4122 19.6774, -29.8885 19.7748, -30.359 19.8668, -30.8356 19.9595, -31.316 20.0515, -31.7922 20.1468, -32.2737 20.2416, -32.7466 20.3233, -33.2345 20.4116, -33.7121 20.4934, -34.1915 20.5885, -34.6732 20.6647, -35.1536 20.7377, -35.6408 20.8238, -36.1195 20.9037, -36.6028 20.9813, -37.0848 18.3408, -37.5781 15.7, -38.0836 13.0592, -38.5934 10.4624, -38.1365 10.3731, -37.6784 10.281, -37.2199 10.1873, -36.7705 10.1055, -36.309 10.005, -35.8581 9.91937, -35.4019 9.81796, -34.9466 9.73141, -34.4863 9.63456, -34.0329 9.53908, -33.5808 9.45101, -33.1235 9.34517, -32.6732 9.25232, -32.2158 9.15274, -31.7686 9.05726, -31.3145 8.95688, -30.8593 8.85547, -30.4067 8.76388, -29.9517 8.66148, -29.5066 8.56088, -29.0522 8.45775, -28.6042 8.3548, -28.1503 8.25088, -27.6983 8.15503, -27.2466 8.04974, -26.7944 7.94329, -26.3478 7.843, -25.8989 7.7325, -25.4409 7.63036, -25.2674 7.59002)))\"},{\"name\":\"mode\",\"content\":\"EO\"},{\"name\":\"platformidentifier\",\"content\":\"2018-039A\"},{\"name\":\"onlinequalitycheck\",\"content\":\"PASSED\"},{\"name\":\"orbitdirection\",\"content\":\"descending\"},{\"name\":\"pduduration\",\"content\":\"179\"},{\"name\":\"passnumber\",\"content\":\"24774\"},{\"name\":\"passdirection\",\"content\":\"descending\"},{\"name\":\"procfacilityname\",\"content\":\"Land SLSTR and SYN Processing and Archiving Centre [LN2]\"},{\"name\":\"procfacilityorg\",\"content\":\"European Space Agency\"},{\"name\":\"processinglevel\",\"content\":\"1\"},{\"name\":\"processingname\",\"content\":\"Data Processing\"},{\"name\":\"productlevel\",\"content\":\"L1\"},{\"name\":\"producttype\",\"content\":\"SL_1_RBT___\"},{\"name\":\"relorbitdir\",\"content\":\"descending\"},{\"name\":\"relpassnumber\",\"content\":\"360\"},{\"name\":\"relpassdirection\",\"content\":\"descending\"},{\"name\":\"platformname\",\"content\":\"Sentinel-3\"},{\"name\":\"size\",\"content\":\"402.69 MB\"},{\"name\":\"timeliness\",\"content\":\"Near Real Time\"},{\"name\":\"pdualongtrackcoord\",\"content\":\"2700\"},{\"name\":\"identifier\",\"content\":\"S3B_SL_1_RBT____20200910T120104_20200910T120404_20200910T135948_0179_043_180_2700_LN2_O_NR_004\"},{\"name\":\"uuid\",\"content\":\"b6216a70-6fbd-4d6d-98e0-e8c479b98686\"}]");
        str.put("entry","[{\"title\":\"S1A_IW_RAW__0SDV_20200910T133908_20200910T133939_034297_03FC92_B68D\",\"link\":[{\"href\":\"https://scihub.copernicus.eu/dhus/odata/v1/Products('883cd3b6-eefe-4c73-b8e7-80759b6403a0')/$value\"},{\"rel\":\"alternative\",\"href\":\"https://scihub.copernicus.eu/dhus/odata/v1/Products('883cd3b6-eefe-4c73-b8e7-80759b6403a0')/\"},{\"rel\":\"icon\",\"href\":\"https://scihub.copernicus.eu/dhus/odata/v1/Products('883cd3b6-eefe-4c73-b8e7-80759b6403a0')/Products('Quicklook')/$value\"}],\"id\":\"883cd3b6-eefe-4c73-b8e7-80759b6403a0\",\"summary\":\"Date: 2020-09-10T13:39:08.248Z, Instrument: SAR-C SAR, Mode: VH VV, Satellite: Sentinel-1, Size: 1.5 GB\",\"date\":[{\"name\":\"beginposition\",\"content\":\"2020-09-10T13:39:08.248Z\"},{\"name\":\"endposition\",\"content\":\"2020-09-10T13:39:39.761Z\"},{\"name\":\"ingestiondate\",\"content\":\"2020-09-10T15:44:44.828Z\"}],\"int\":[{\"name\":\"missiondatatakeid\",\"content\":\"261266\"},{\"name\":\"orbitnumber\",\"content\":\"34297\"},{\"name\":\"lastorbitnumber\",\"content\":\"34297\"},{\"name\":\"relativeorbitnumber\",\"content\":\"100\"},{\"name\":\"lastrelativeorbitnumber\",\"content\":\"100\"},{\"name\":\"slicenumber\",\"content\":\"19\"}],\"str\":[{\"name\":\"sensoroperationalmode\",\"content\":\"IW\"},{\"name\":\"orbitdirection\",\"content\":\"DESCENDING\"},{\"name\":\"producttype\",\"content\":\"RAW\"},{\"name\":\"platformname\",\"content\":\"Sentinel-1\"},{\"name\":\"platformidentifier\",\"content\":\"2014-016A\"},{\"name\":\"instrumentname\",\"content\":\"Synthetic Aperture Radar (C-band)\"},{\"name\":\"instrumentshortname\",\"content\":\"SAR-C SAR\"},{\"name\":\"filename\",\"content\":\"S1A_IW_RAW__0SDV_20200910T133908_20200910T133939_034297_03FC92_B68D.SAFE\"},{\"name\":\"format\",\"content\":\"SAFE\"},{\"name\":\"productclass\",\"content\":\"S\"},{\"name\":\"polarisationmode\",\"content\":\"VH VV\"},{\"name\":\"acquisitiontype\",\"content\":\"NOMINAL\"},{\"name\":\"status\",\"content\":\"ARCHIVED\"},{\"name\":\"size\",\"content\":\"1.5 GB\"},{\"name\":\"gmlfootprint\",\"content\":\"<gml:Polygon srsName=\\\"http://www.opengis.net/gml/srs/epsg.xml#4326\\\" xmlns:gml=\\\"http://www.opengis.net/gml\\\">\\n   <gml:outerBoundaryIs>\\n      <gml:LinearRing>\\n         <gml:coordinates>23.6791,-116.8500 21.7742,-117.2181 21.5003,-114.8669 23.4070,-114.4653 23.6791,-116.8500 23.6791,-116.8500</gml:coordinates>\\n      </gml:LinearRing>\\n   </gml:outerBoundaryIs>\\n</gml:Polygon>\"},{\"name\":\"footprint\",\"content\":\"MULTIPOLYGON (((-114.8669 21.5003, -114.4653 23.407, -116.85 23.6791, -117.2181 21.7742, -114.8669 21.5003)))\"},{\"name\":\"identifier\",\"content\":\"S1A_IW_RAW__0SDV_20200910T133908_20200910T133939_034297_03FC92_B68D\"},{\"name\":\"productconsolidation\",\"content\":\"SLICE\"},{\"name\":\"uuid\",\"content\":\"883cd3b6-eefe-4c73-b8e7-80759b6403a0\"}]},{\"title\":\"S2B_MSIL2A_20200910T064629_N0214_R020_T39PWK_20200910T105234\",\"link\":[{\"href\":\"https://scihub.copernicus.eu/dhus/odata/v1/Products('def336bc-9d68-4847-97ac-4429a78fb9f3')/$value\"},{\"rel\":\"alternative\",\"href\":\"https://scihub.copernicus.eu/dhus/odata/v1/Products('def336bc-9d68-4847-97ac-4429a78fb9f3')/\"},{\"rel\":\"icon\",\"href\":\"https://scihub.copernicus.eu/dhus/odata/v1/Products('def336bc-9d68-4847-97ac-4429a78fb9f3')/Products('Quicklook')/$value\"}],\"id\":\"def336bc-9d68-4847-97ac-4429a78fb9f3\",\"summary\":\"Date: 2020-09-10T06:46:29.024Z, Instrument: MSI, Mode: , Satellite: Sentinel-2, Size: 914.98 MB\",\"date\":[{\"name\":\"beginposition\",\"content\":\"2020-09-10T06:46:29.024Z\"},{\"name\":\"endposition\",\"content\":\"2020-09-10T06:46:29.024Z\"},{\"name\":\"ingestiondate\",\"content\":\"2020-09-10T15:44:30.854Z\"}],\"int\":[{\"name\":\"orbitnumber\",\"content\":\"18350\"},{\"name\":\"relativeorbitnumber\",\"content\":\"20\"}],\"double\":[{\"name\":\"vegetationpercentage\",\"content\":\"9.3E-5\"},{\"name\":\"notvegetatedpercentage\",\"content\":\"0.001198\"},{\"name\":\"waterpercentage\",\"content\":\"95.612085\"},{\"name\":\"unclassifiedpercentage\",\"content\":\"0.0\"},{\"name\":\"mediumprobacloudspercentage\",\"content\":\"0.387882\"},{\"name\":\"highprobacloudspercentage\",\"content\":\"0.181612\"},{\"name\":\"snowicepercentage\",\"content\":\"0.0\"},{\"name\":\"cloudcoverpercentage\",\"content\":\"4.386624\"}],\"str\":[{\"name\":\"level1cpdiidentifier\",\"content\":\"S2B_OPER_MSI_L1C_TL_EPAE_20200910T093254_A018350_T39PWK_N02.09\"},{\"name\":\"gmlfootprint\",\"content\":\"<gml:Polygon srsName=\\\"http://www.opengis.net/gml/srs/epsg.xml#4326\\\" xmlns:gml=\\\"http://www.opengis.net/gml\\\">\\n   <gml:outerBoundaryIs>\\n      <gml:LinearRing>\\n         <gml:coordinates>9.046743365203026,50.99981801611545 9.045382492111987,51.99885736736441 8.052359601205822,51.99627506480048 8.053569047879877,50.999818486685434 9.046743365203026,50.99981801611545</gml:coordinates>\\n      </gml:LinearRing>\\n   </gml:outerBoundaryIs>\\n</gml:Polygon>\"},{\"name\":\"footprint\",\"content\":\"MULTIPOLYGON (((51.99627506480048 8.052359601205822, 51.99885736736441 9.045382492111987, 50.99981801611545 9.046743365203026, 50.999818486685434 8.053569047879877, 51.99627506480048 8.052359601205822)))\"},{\"name\":\"format\",\"content\":\"SAFE\"},{\"name\":\"processingbaseline\",\"content\":\"02.14\"},{\"name\":\"platformname\",\"content\":\"Sentinel-2\"},{\"name\":\"filename\",\"content\":\"S2B_MSIL2A_20200910T064629_N0214_R020_T39PWK_20200910T105234.SAFE\"},{\"name\":\"instrumentname\",\"content\":\"Multi-Spectral Instrument\"},{\"name\":\"instrumentshortname\",\"content\":\"MSI\"},{\"name\":\"size\",\"content\":\"914.98 MB\"},{\"name\":\"s2datatakeid\",\"content\":\"GS2B_20200910T064629_018350_N02.14\"},{\"name\":\"producttype\",\"content\":\"S2MSI2A\"},{\"name\":\"platformidentifier\",\"content\":\"2017-013A\"},{\"name\":\"orbitdirection\",\"content\":\"DESCENDING\"},{\"name\":\"platformserialidentifier\",\"content\":\"Sentinel-2B\"},{\"name\":\"processinglevel\",\"content\":\"Level-2A\"},{\"name\":\"identifier\",\"content\":\"S2B_MSIL2A_20200910T064629_N0214_R020_T39PWK_20200910T105234\"},{\"name\":\"uuid\",\"content\":\"def336bc-9d68-4847-97ac-4429a78fb9f3\"}]}]");

    }

    @After
    public void closeInputStream() throws IOException {
        jsonContent.close();
    }

    @Test
    public void parser_string_array_to_productProperties() throws JSONException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<ProductProperties> asList = mapper.readValue(str.get("str").toString(), typeRef);
        assertThat(asList.size()).isGreaterThan(0);
    }

    @Test
    public void given_sentinel1_data_should_return_sentinel1_deserializer() throws JSONException, IOException {
        SentinelOpenSearchProductDeserializer s1 = SentinelOpenSearchDeserializerManager.getDeserializer("S1");
        assertThat(s1).isInstanceOf(Sentinel1OpenSearchProductDeserializer.class);
    }

    @Test
    public void given_json_data_should_return_products_object() throws JSONException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<ProductDTO> products = mapper.readValue(str.get("entry").toString(), new TypeReference<List<ProductDTO>>() { });
        assertThat(products.size()).isEqualTo(2);
    }

    @Test
    public void given_a_sentinel1_product_should_return_sentinel1POJO() throws JSONException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        List<ProductDTO> products = mapper.readValue(str.get("entry").toString(), new TypeReference<List<ProductDTO>>() { });
        ProductDTO p = products.get(0);
        assertThat(p).isInstanceOf(Sentinel1ProductDTO.class);
        assertThat(p.getTitle()).isEqualTo("S1A_IW_RAW__0SDV_20200910T133908_20200910T133939_034297_03FC92_B68D");
        assertThat(p.getId()).isEqualTo("883cd3b6-eefe-4c73-b8e7-80759b6403a0");
        assertThat(p.getPlatformName()).isEqualTo("Sentinel-1");
        assertThat(((Sentinel1ProductDTO)p).getPolarizationMode()).isEqualTo("VH VV");
    }

    @Test
    public void given_a_sentinel2_product_should_return_sentinel2POJO() throws JSONException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<ProductDTO> products = mapper.readValue(str.get("entry").toString(), new TypeReference<List<ProductDTO>>() { });
        ProductDTO p = products.get(1);
        assertThat(p).isInstanceOf(Sentinel2ProductDTO.class);
        assertThat(((Sentinel2ProductDTO)p).getCloudCoverPercentage()).isEqualTo(4.386624d);
    }

    @Test
    public void given_a_json_data_with_one_entry_should_return_OpenDataResultPOJO() throws IOException {
        OpenSearchResponse response = (OpenSearchResponse) ProductDeserializerFactory.get("OpenSearch").deserialize(jsonContentOne);
        assertThat(response.getNumOfProducts()).isEqualTo(30432470);
        assertThat(response.getRows()).isEqualTo(75);
        assertThat(response.getStartIndex()).isEqualTo(1);
        assertThat(response.getProducts().size()).isEqualTo(1);
        assertThat(response.getProducts().get(0)).isInstanceOf(Sentinel1ProductDTO.class);
        assertThat(((Sentinel1ProductDTO)response.getProducts().get(0)).getPolarizationMode()).isEqualTo("VV");
    }

    @Test
    public void given_a_json_data_should_return_OpenDataResultPOJO() throws IOException {
        OpenSearchResponse response = (OpenSearchResponse) ProductDeserializerFactory.get("OpenSearch").deserialize(jsonContent);
        assertThat(response.getNumOfProducts()).isEqualTo(30432470);
        assertThat(response.getProducts().size()).isEqualTo(4);
        assertThat(response.getProducts().get(0)).isInstanceOf(Sentinel1ProductDTO.class);
        assertThat(((Sentinel1ProductDTO)response.getProducts().get(0)).getPolarizationMode()).isEqualTo("VV");
        assertThat(response.getProducts().get(1)).isInstanceOf(Sentinel2ProductDTO.class);
        assertThat(((Sentinel2ProductDTO)response.getProducts().get(1)).getCloudCoverPercentage()).isEqualTo(51.9229d);
    }

    @Test
    public void given_a_json_data_with_no_entries_should_return_an_empty_OpenDataResultPOJO() throws JAXBException, IOException {
        InputStream jsonWithNoEntries = new FileInputStream(Paths.get("src/test/java/utils/empty.json").toFile());
        OpenSearchResponse response = (OpenSearchResponse) ProductDeserializerFactory.get("OpenSearch").deserialize(jsonWithNoEntries);
        assertThat(response.getNumOfProducts()).isEqualTo(0);
        assertThat(response.getProducts().size()).isEqualTo(0);
        jsonWithNoEntries.close();
    }


}

package model;

import model.products.Product;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class Product_ {
    public static final String TITLE = "S1A_IW_RAW__0SDV_20200910T070039_20200910T070111_034293_03FC6D_E4C0";
    public static final String ID = "2f4f7537-1711-4871-af8b-9f3ce98a1f9a";
    public static final String INGESTION_TIME = "2020-09-10T08:50:57.572Z";
    public static final String FOOTPRINT = "MULTIPOLYGON (((-13.3525 34.187, -12.8959 36.142, -15.6169 36.4117, -16.0074 34.4559, -13.3525 34.187)))";
    public static final String PRODUCT_TYPE = "RAW";
    public static final String PLATFORM_NAME = "Sentinel-1";
    public static final String SIZE = "1.55 GB";
    public static final String STATUS = "ARCHIVED";
    public static final Calendar calendar = DatatypeConverter.parseDateTime(INGESTION_TIME);
    Product product;

    @Before
    public void initMockup() {
        product = new Product();

    }

    @Test
    public void should_return_same_id_as_seted() {
        product.setId(ID);
        assertThat(product.getId()).isEqualTo(ID);
    }

    @Test
    public void should_return_same_size_as_seted() {
        product.setSize(SIZE);
        assertThat(product.getSize()).isEqualTo(SIZE);
    }

    @Test
    public void should_return_same_title_as_seted() {
        product.setTitle(TITLE);
        assertThat(product.getTitle()).isEqualTo(TITLE);
    }

    @Test
    public void should_return_same_footprint_as_seted() {
        product.setFootprint(FOOTPRINT);
        assertThat(product.getFootprint()).isEqualTo(FOOTPRINT);
    }

    @Test
    public void should_return_same_ingestionDate_as_seted() {
        product.setIngestionDate(INGESTION_TIME);
        Calendar calendar = DatatypeConverter.parseDateTime(INGESTION_TIME);
        assertThat(product.getIngestionDate()).isEqualTo(calendar);
    }

    @Test
    public void should_return_same_status_as_seted() {
        product.setStatus(STATUS);
        assertThat(product.getStatus()).isEqualTo(STATUS);
    }

    @Test
    public void should_return_same_productType_as_seted() {
        product.setProductType(PRODUCT_TYPE);
        assertThat(product.getProductType()).isEqualTo(PRODUCT_TYPE);
    }

    @Test
    public void should_return_same_plataformName_as_seted() {
        product.setPlatformName(PLATFORM_NAME);
        assertThat(product.getPlatformName()).isEqualTo(PLATFORM_NAME);
    }

    @Test
    public void test_constructor() {
        Product p = new Product(INGESTION_TIME,TITLE,ID,FOOTPRINT,SIZE,PRODUCT_TYPE,PLATFORM_NAME,STATUS);



        assertThat(p.getIngestionDate()).isEqualTo(calendar);
        assertThat(p.getTitle()).isEqualTo(TITLE);
        assertThat(p.getId()).isEqualTo(ID);
        assertThat(p.getFootprint()).isEqualTo(FOOTPRINT);
        assertThat(p.getSize()).isEqualTo(SIZE);
        assertThat(p.getProductType()).isEqualTo(PRODUCT_TYPE);
        assertThat(p.getPlatformName()).isEqualTo(PLATFORM_NAME);
        assertThat(p.getStatus()).isEqualTo(STATUS);
    }

    @Test
    public void should_return_size_as_double() {
        product.setSize(SIZE);
        assertThat(product.getSizeAsDouble()).isEqualTo(1.55d);
    }

    @Test
    public void should_return_size_in_Mb_as_double_less_than_one() {
        product.setSize("766 MB");
        assertThat(product.getSizeAsDouble()).isEqualTo(766.0/1024.0);
    }

    /*@Test
    public void with_content_length_in_bytes_should_return_gigabytes() throws NoSuchFieldException, IllegalAccessException {
        Product product = new Product();
        product.setField("ContentLength", CONTENT_LENGTH);
        assertThat(product.getGigaBytes()).isEqualTo(0.156);

        product.setField("ContentLength", (long)534825600);
        assertThat(product.getGigaBytes()).isEqualTo(0.534);

        product.setField("ContentLength",  (long)1007388501);
        assertThat(product.getGigaBytes()).isEqualTo(1.007);

        product.setField("ContentLength",  (long)4452877361.0);
        assertThat(product.getGigaBytes()).isEqualTo(4.452);
    }*/



}

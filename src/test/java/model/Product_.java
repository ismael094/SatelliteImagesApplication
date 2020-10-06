package model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import org.apache.logging.log4j.core.appender.routing.Route;
import org.junit.Before;
import org.junit.Test;
import services.entities.Product;

import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.Field;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

public class Product_ {

    ProductDTO product;

    @Before
    public void initMockup() {

        product = SentinelData.getProduct();
    }

    @Test
    public void should_return_same_id_as_seted() {
        product.setId(SentinelData.ID);
        assertThat(product.getId()).isEqualTo(SentinelData.ID);
    }

    @Test
    public void should_return_same_size_as_seted() {
        product.setSize(SentinelData.SIZE);
        assertThat(product.getSize()).isEqualTo(SentinelData.SIZE);
    }

    @Test
    public void should_return_same_title_as_seted() {
        product.setTitle(SentinelData.TITLE);
        assertThat(product.getTitle()).isEqualTo(SentinelData.TITLE);
    }

    @Test
    public void should_return_same_footprint_as_seted() {
        product.setFootprint(SentinelData.FOOTPRINT);
        assertThat(product.getFootprint()).isEqualTo(SentinelData.FOOTPRINT);
    }

    @Test
    public void should_return_same_ingestionDate_as_seted() {
        product.setIngestionDate(SentinelData.INGESTION_TIME);
        Calendar calendar = DatatypeConverter.parseDateTime(SentinelData.INGESTION_TIME);
        assertThat(product.getIngestionDate()).isEqualTo(calendar);
    }

    @Test
    public void should_return_same_status_as_seted() {
        product.setStatus(SentinelData.STATUS);
        assertThat(product.getStatus()).isEqualTo(SentinelData.STATUS);
    }

    @Test
    public void should_return_same_productType_as_seted() {
        product.setProductType(SentinelData.PRODUCT_TYPE);
        assertThat(product.getProductType()).isEqualTo(SentinelData.PRODUCT_TYPE);
    }

    @Test
    public void should_return_same_plataformName_as_seted() {
        product.setPlatformName(SentinelData.PLATFORM_NAME);
        assertThat(product.getPlatformName()).isEqualTo(SentinelData.PLATFORM_NAME);
    }

    @Test
    public void test_constructor() {
        ProductDTO p = new ProductDTO(
                new SimpleStringProperty(SentinelData.ID),new SimpleStringProperty(SentinelData.TITLE),
                new SimpleStringProperty(SentinelData.PLATFORM_NAME),new SimpleStringProperty(SentinelData.PRODUCT_TYPE),
                new SimpleStringProperty(SentinelData.FOOTPRINT),new SimpleStringProperty(SentinelData.SIZE),
                new SimpleStringProperty(SentinelData.STATUS),
                new SimpleObjectProperty<>(SentinelData.calendar));



        assertThat(p.getIngestionDate()).isEqualTo(SentinelData.calendar);
        assertThat(p.getTitle()).isEqualTo(SentinelData.TITLE);
        assertThat(p.getId()).isEqualTo(SentinelData.ID);
        assertThat(p.getFootprint()).isEqualTo(SentinelData.FOOTPRINT);
        assertThat(p.getSize()).isEqualTo(SentinelData.SIZE);
        assertThat(p.getProductType()).isEqualTo(SentinelData.PRODUCT_TYPE);
        assertThat(p.getPlatformName()).isEqualTo(SentinelData.PLATFORM_NAME);
        assertThat(p.getStatus()).isEqualTo(SentinelData.STATUS);
    }

    @Test
    public void should_return_size_as_double() {
        product.setSize(SentinelData.SIZE);
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

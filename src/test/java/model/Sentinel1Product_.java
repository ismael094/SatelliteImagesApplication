package model;

import model.products.Sentinel1Product;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel1Product_ {
    public static final String SENSOR_OPERATIONAL_MODE = "IW";
    public static final String POLARISATION_MODE = "VH VV";
    private Sentinel1Product product;

    @Before
    public void initMockup() {
        product = new Sentinel1Product();
    }

    @Test
    public void should_return_same_sensor_mode_as_seted() {
        product.setSensorOperationalMode(SENSOR_OPERATIONAL_MODE);
        assertThat(product.getSensorOperationalMode()).isEqualTo(SENSOR_OPERATIONAL_MODE);
    }

    @Test
    public void should_return_same_polarisation_mode_as_seted() {
        product.setPolarizationMode(POLARISATION_MODE);
        assertThat(product.getPolarizationMode()).isEqualTo(POLARISATION_MODE);
    }

    @Test
    public void contructor_test() {
        Sentinel1Product p = new Sentinel1Product(SENSOR_OPERATIONAL_MODE,POLARISATION_MODE);
        assertThat(p.getSensorOperationalMode()).isEqualTo(SENSOR_OPERATIONAL_MODE);
        assertThat(p.getPolarizationMode()).isEqualTo(POLARISATION_MODE);
    }

    @Test
    public void super_contructor_test() {
        Sentinel1Product p = new Sentinel1Product(Product_.INGESTION_TIME,Product_.TITLE,Product_.ID,Product_.FOOTPRINT,Product_.SIZE,Product_.PRODUCT_TYPE,
                Product_.PLATFORM_NAME,Product_.STATUS,SENSOR_OPERATIONAL_MODE,POLARISATION_MODE);
        assertThat(p.getSensorOperationalMode()).isEqualTo(SENSOR_OPERATIONAL_MODE);
        assertThat(p.getPolarizationMode()).isEqualTo(POLARISATION_MODE);

        assertThat(p.getIngestionDate()).isEqualTo(Product_.calendar);
        assertThat(p.getTitle()).isEqualTo(Product_.TITLE);
        assertThat(p.getId()).isEqualTo(Product_.ID);
        assertThat(p.getFootprint()).isEqualTo(Product_.FOOTPRINT);
        assertThat(p.getSize()).isEqualTo(Product_.SIZE);
        assertThat(p.getProductType()).isEqualTo(Product_.PRODUCT_TYPE);
        assertThat(p.getPlatformName()).isEqualTo(Product_.PLATFORM_NAME);
        assertThat(p.getStatus()).isEqualTo(Product_.STATUS);
    }

}

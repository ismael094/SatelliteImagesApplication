package model;

import model.products.Sentinel1ProductDTO;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel1Product_ {

    private Sentinel1ProductDTO product;

    @Before
    public void initMockup() {
        product = SentinelData.getSentinel1Product();
    }

    @Test
    public void should_return_same_sensor_mode_as_seted() {
        product.setSensorOperationalMode(SentinelData.SENSOR_OPERATIONAL_MODE);
        assertThat(product.getSensorOperationalMode()).isEqualTo(SentinelData.SENSOR_OPERATIONAL_MODE);
    }

    @Test
    public void should_return_same_polarisation_mode_as_seted() {
        product.setPolarizationMode(SentinelData.POLARISATION_MODE);
        assertThat(product.getPolarizationMode()).isEqualTo(SentinelData.POLARISATION_MODE);
    }

    @Test
    public void constructor_test() {
        assertThat(product.getSensorOperationalMode()).isEqualTo(SentinelData.SENSOR_OPERATIONAL_MODE);
        assertThat(product.getPolarizationMode()).isEqualTo(SentinelData.POLARISATION_MODE);
    }

    @Test
    public void super_constructor_test() {
        assertThat(product.getSensorOperationalMode()).isEqualTo(SentinelData.SENSOR_OPERATIONAL_MODE);
        assertThat(product.getPolarizationMode()).isEqualTo(SentinelData.POLARISATION_MODE);

        assertThat(product.getIngestionDate()).isEqualTo(SentinelData.calendar);
        assertThat(product.getTitle()).isEqualTo(SentinelData.TITLE);
        assertThat(product.getId()).isEqualTo(SentinelData.ID);
        assertThat(product.getFootprint()).isEqualTo(SentinelData.FOOTPRINT);
        assertThat(product.getSize()).isEqualTo(SentinelData.SIZE);
        assertThat(product.getProductType()).isEqualTo(SentinelData.PRODUCT_TYPE);
        assertThat(product.getPlatformName()).isEqualTo(SentinelData.PLATFORM_NAME);
        assertThat(product.getStatus()).isEqualTo(SentinelData.STATUS);
    }

}

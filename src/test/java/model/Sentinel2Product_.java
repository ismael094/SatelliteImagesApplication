package model;

import model.products.sentinel.Sentinel2ProductDTO;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel2Product_ {


    private Sentinel2ProductDTO product;

    @Before
    public void initMockup() {
        product = SentinelData.getSentinel2Product();
    }

    @Test
    public void should_return_same_cloud_percentage_as_seted() {
        product.setCloudCoverPercentage(SentinelData.CLOUD_PERCENTAGE_COVERAGE);
        assertThat(product.getCloudCoverPercentage()).isEqualTo(SentinelData.CLOUD_PERCENTAGE_COVERAGE);
    }

    @Test
    public void should_return_same_vegetation_percentage_as_seted() {
        product.setVegetationPercentageCoverage(SentinelData.VEGETATION_PERCENTAGE_COVERAGE);
        assertThat(product.getVegetationPercentageCoverage()).isEqualTo(SentinelData.VEGETATION_PERCENTAGE_COVERAGE);
    }

    @Test
    public void should_return_same_not_vegetation_percentage_as_seted() {
        product.setNotVegetationPercentageCoverage(SentinelData.NOT_VEGETATION_PERCENTAGE_COVERAGE);
        assertThat(product.getNotVegetationPercentageCoverage()).isEqualTo(SentinelData.NOT_VEGETATION_PERCENTAGE_COVERAGE);
    }

    @Test
    public void should_return_same_water_percentage_as_seted() {
        product.setWaterPercentageCoverage(SentinelData.WATER_PERCENTAGE_COVERAGE);
        assertThat(product.getWaterPercentageCoverage()).isEqualTo(SentinelData.WATER_PERCENTAGE_COVERAGE);
    }

    @Test
    public void constructor_test() {
        assertThat(product.getCloudCoverPercentage()).isEqualTo(SentinelData.CLOUD_PERCENTAGE_COVERAGE);
    }

    @Test
    public void super_constructor_test() {
        assertThat(product.getCloudCoverPercentage()).isEqualTo(SentinelData.CLOUD_PERCENTAGE_COVERAGE);
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

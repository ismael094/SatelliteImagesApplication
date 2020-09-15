package model;

import model.products.Sentinel2Product;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel2Product_ {
    public final double CLOUD_PERCENTAGE_COVERAGE = 67.98189;
    public final double VEGETATION_PERCENTAGE_COVERAGE = 0.002322;
    public final double NOT_VEGETATION_PERCENTAGE_COVERAGE = 0.002412;
    public final double WATER_PERCENTAGE_COVERAGE = 32.013375;

    private Sentinel2Product product;

    @Before
    public void initMockup() {
        product = new Sentinel2Product();
    }

    @Test
    public void should_return_same_cloud_percentage_as_seted() {
        product.setCloudCoverPercentage(CLOUD_PERCENTAGE_COVERAGE);
        assertThat(product.getCloudCoverPercentage()).isEqualTo(CLOUD_PERCENTAGE_COVERAGE);
    }

    @Test
    public void should_return_same_vegetation_percentage_as_seted() {
        product.setVegetationPercentageCoverage(VEGETATION_PERCENTAGE_COVERAGE);
        assertThat(product.getVegetationPercentageCoverage()).isEqualTo(VEGETATION_PERCENTAGE_COVERAGE);
    }

    @Test
    public void should_return_same_not_vegetation_percentage_as_seted() {
        product.setNotVegetationPercentageCoverage(NOT_VEGETATION_PERCENTAGE_COVERAGE);
        assertThat(product.getNotVegetationPercentageCoverage()).isEqualTo(NOT_VEGETATION_PERCENTAGE_COVERAGE);
    }

    @Test
    public void should_return_same_water_percentage_as_seted() {
        product.setWaterPercentageCoverage(WATER_PERCENTAGE_COVERAGE);
        assertThat(product.getWaterPercentageCoverage()).isEqualTo(WATER_PERCENTAGE_COVERAGE);
    }

    @Test
    public void contructor_test() {
        Sentinel2Product p = new Sentinel2Product(CLOUD_PERCENTAGE_COVERAGE);
        assertThat(p.getCloudCoverPercentage()).isEqualTo(CLOUD_PERCENTAGE_COVERAGE);
    }

    @Test
    public void super_contructor_test() {
        Sentinel2Product p = new Sentinel2Product(Product_.INGESTION_TIME,Product_.TITLE,Product_.ID,Product_.FOOTPRINT,Product_.SIZE,Product_.PRODUCT_TYPE,
                Product_.PLATFORM_NAME,Product_.STATUS,CLOUD_PERCENTAGE_COVERAGE);
        assertThat(p.getCloudCoverPercentage()).isEqualTo(CLOUD_PERCENTAGE_COVERAGE);
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

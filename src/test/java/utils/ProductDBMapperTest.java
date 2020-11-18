package utils;

import model.SentinelData;
import model.products.ProductDTO;
import model.products.sentinel.Sentinel1ProductDTO;
import org.junit.Test;
import services.database.mappers.ProductDAOMapper;
import services.database.mappers.ProductMapper;
import services.database.mappers.productmappers.Sentinel1ProductMapper;
import services.database.mappers.productmappers.Sentinel2ProductMapper;
import services.entities.Product;
import services.entities.Sentinel1Product;
import services.entities.Sentinel2Product;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDBMapperTest {

    private ProductDAOMapper mapper = new ProductDAOMapper();

    @Test
    public void get_sentinel1_mapper() {
        ProductMapper mapper = ProductDAOMapper.getMapper(SentinelData.getSentinel1Product());
        assertThat(mapper).isNotNull();
        assertThat(mapper).isInstanceOf(Sentinel1ProductMapper.class);
    }

    @Test
    public void get_sentinel2_mapper() {
        ProductMapper mapper = ProductDAOMapper.getMapper(SentinelData.getSentinel2Product());
        assertThat(mapper).isNotNull();
        assertThat(mapper).isInstanceOf(Sentinel2ProductMapper.class);
    }

    @Test
    public void get_sentinel1_entity() {
        Product product = mapper.toEntity(SentinelData.getSentinel1Product());
        assertThat(product).isInstanceOf(Sentinel1Product.class);
    }

    @Test
    public void get_sentinel1_productDto() {
        Product product = mapper.toEntity(SentinelData.getSentinel1Product());
        assertThat(mapper.toDTO(product)).isInstanceOf(Sentinel1ProductDTO.class);
    }

    @Test
    public void get_default_productDto() {
        ProductDTO product1 = SentinelData.getProduct();
        product1.setPlatformName("Sentinel-3");
        Product product = mapper.toEntity(product1);
        assertThat(product).isNotNull();
        assertThat(product).isNotInstanceOf(Sentinel1Product.class);
        assertThat(product).isNotInstanceOf(Sentinel2Product.class);
    }


}

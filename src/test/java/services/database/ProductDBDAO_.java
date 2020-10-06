package services.database;

import dev.morphia.query.experimental.filters.Filters;
import model.SentinelData;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import org.junit.Before;
import org.junit.Test;
import services.entities.Product;
import utils.database.MongoDBManager;
import utils.MongoDB_;

import javax.xml.bind.DatatypeConverter;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDBDAO_ {

    private MongoDBManager mongodb;
    private ProductDBDAO productDAO;
    private ProductDTO productDTO;
    private Sentinel1ProductDTO sentinelDAO;

    @Before
    public void init() {
        SentinelData.calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        mongodb = MongoDBManager.getMongoDBManager();
        if (!mongodb.isConnected()) {
            mongodb.setCredentialsAndDatabase(MongoDB_.USER,MongoDB_.PASSWORD,MongoDB_.DATABASE);
            mongodb.connect();
        }
        productDAO = ProductDBDAO.getInstance();

        productDTO = SentinelData.getProduct();
        productDTO.setId(SentinelData.ID);
        productDTO.setTitle(SentinelData.TITLE);
        productDTO.setFootprint(SentinelData.FOOTPRINT);
        productDTO.setPlatformName(SentinelData.PLATFORM_NAME);
        productDTO.setProductType(SentinelData.PRODUCT_TYPE);
        productDTO.setSize(SentinelData.SIZE);
        productDTO.setStatus(SentinelData.STATUS);
        productDTO.setIngestionDate(SentinelData.INGESTION_TIME);

        sentinelDAO = SentinelData.getSentinel1Product();

        sentinelDAO.setIngestionDate(SentinelData.INGESTION_TIME);
    }

    @Test
    public void save_and_delete_user_collection() {
        productDAO.save(productDTO);
        ProductDTO dbProductDTO = productDAO.findFirst(productDTO);
        assertThat(dbProductDTO).isNotNull();
        assertThat(dbProductDTO.getId()).isEqualTo(productDTO.getId());
        productDAO.delete(dbProductDTO);
        dbProductDTO = productDAO.findFirst(productDTO);
        assertThat(dbProductDTO).isNull();
    }

    @Test
    public void save_and_delete_sentinel1() {
        productDAO.save(sentinelDAO);
        ProductDTO dbProductDTO = productDAO.findFirst(sentinelDAO);
        assertThat(dbProductDTO).isNotNull();
        assertThat(dbProductDTO.getId()).isEqualTo(sentinelDAO.getId());
        assertThat(dbProductDTO.getIngestionDate()).isEqualTo(sentinelDAO.getIngestionDate());
        productDAO.delete(dbProductDTO);
        dbProductDTO = productDAO.findFirst(sentinelDAO);
        assertThat(dbProductDTO).isNull();
    }

    @Test
    public void productDTO_to_entity() {
        Product product = productDAO.toEntity(productDTO);
        assertThat(product.getId()).isEqualTo(productDTO.getId());
        assertThat(product.getFootprint()).isEqualTo(productDTO.getFootprint());
        assertThat(product.getProductType()).isEqualTo(productDTO.getProductType());
        assertThat(DatatypeConverter.parseDateTime(product.getIngestionDate())).isEqualTo(productDTO.getIngestionDate());
    }

    @Test
    public void entity_to_ProductDTO() {
        productDAO.save(productDTO);
        Product product = mongodb.getDatastore().find(Product.class).filter(Filters.eq("id", productDTO.getId())).first();
        ProductDTO productDTO = productDAO.toDAO(product);
        assertThat(product.getId()).isEqualTo(productDTO.getId());
        assertThat(product.getFootprint()).isEqualTo(productDTO.getFootprint());
        assertThat(product.getProductType()).isEqualTo(productDTO.getProductType());
        productDAO.delete(productDTO);
    }
}

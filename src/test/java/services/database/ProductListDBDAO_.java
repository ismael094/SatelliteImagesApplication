package services.database;

import javafx.beans.property.SimpleStringProperty;
import model.SentinelData;
import model.list.ProductListDTO;
import model.restriction.PlatformRestriction;
import model.restriction.ProductTypeRestriction;
import org.junit.Before;
import org.junit.Test;
import utils.database.MongoDBManager;
import utils.MongoDB_;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductListDBDAO_ {
    private ProductListDTO productListDTO;
    private ProductListDBDAO productListDAO;
    private MongoDBManager mongodb;

    @Before
    public void init() {
        mongodb = MongoDBManager.getMongoDBManager();
        if (!mongodb.isConnected()) {
            mongodb.setCredentialsAndDatabase(MongoDB_.USER,MongoDB_.PASSWORD,MongoDB_.DATABASE);
            mongodb.connect();
        }
        productListDTO = new ProductListDTO(new SimpleStringProperty("list"), new SimpleStringProperty("description"));
        productListDAO = ProductListDBDAO.getInstance();
    }

    @Test
    public void get_collection() {
        List<ProductListDTO> productListDTOS = productListDAO.getCollection();
        assertThat(productListDTOS.size()).isGreaterThan(0);

    }

    @Test
    public void save_and_delete_empty_product_list() {
        productListDAO.save(productListDTO);
        List<ProductListDTO> productListDTOS = productListDAO.find(productListDTO);
        assertThat(productListDTOS.size()).isEqualTo(1);
        assertThat(productListDTOS.get(0).getName()).isEqualTo(productListDTO.getName());
        assertThat(productListDTOS.get(0).getId()).isEqualTo(productListDTO.getId());
        productListDAO.delete(productListDTO);
        productListDTOS = productListDAO.find(productListDTO);
        assertThat(productListDTOS.size()).isEqualTo(0);
    }

    @Test
    public void save_and_delete_with_products_product_list() {
        productListDTO.addProduct(SentinelData.getProduct());
        productListDAO.save(productListDTO);
        List<ProductListDTO> productListDTOS = productListDAO.find(productListDTO);
        assertThat(productListDTOS.size()).isEqualTo(1);
        assertThat(productListDTOS.get(0).getName()).isEqualTo(productListDTO.getName());
        assertThat(productListDTOS.get(0).getId()).isEqualTo(productListDTO.getId());
        assertThat(productListDTOS.get(0).getProducts().get(0).getId()).isEqualTo(SentinelData.getProduct().getId());
        productListDAO.delete(productListDTO);
        productListDTOS = productListDAO.find(productListDTO);
        assertThat(productListDTOS.size()).isEqualTo(0);
    }

    @Test
    public void save_and_delete_with_products_and_restrictions_product_list() {
        productListDTO.addProduct(SentinelData.getProduct());
        PlatformRestriction platFormRestriction = new PlatformRestriction();
        platFormRestriction.add("Sentinel-1");
        //ProductTypeRestriction productTypeRestriction = new ProductTypeRestriction();
        //productTypeRestriction.add("GRD");
        productListDTO.addRestriction(platFormRestriction);
        //productListDTO.addRestriction(productTypeRestriction);
        productListDAO.save(productListDTO);
        List<ProductListDTO> productListDTOS = productListDAO.find(productListDTO);
        assertThat(productListDTOS.size()).isEqualTo(1);
        assertThat(productListDTOS.get(0).getName()).isEqualTo(productListDTO.getName());
        assertThat(productListDTOS.get(0).getId()).isEqualTo(productListDTO.getId());
        assertThat(productListDTOS.get(0).getProducts().size()).isGreaterThan(0);
        assertThat(productListDTOS.get(0).getProducts().get(0).getId()).isEqualTo(SentinelData.getProduct().getId());
        assertThat(productListDTOS.get(0).getRestrictions().get(0).getValues()).isEqualTo(platFormRestriction.getValues());
        //assertThat(productListDTOS.get(0).getRestrictions().get(1).getValues()).isEqualTo(productTypeRestriction.getValues());
        productListDAO.delete(productListDTO);
        productListDTOS = productListDAO.find(productListDTO);
        assertThat(productListDTOS.size()).isEqualTo(0);
    }

    @Test
    public void save_and_delete_with_products_and_restrictions_and_areas_product_list() {
        productListDTO.addProduct(SentinelData.getProduct());
        PlatformRestriction platFormRestriction = new PlatformRestriction();
        platFormRestriction.add("Sentinel-1");
        productListDTO.addRestriction(platFormRestriction);
        productListDTO.addAreaOfWork("AREA1");
        productListDTO.addAreaOfWork("AREA2");
        productListDAO.save(productListDTO);
        List<ProductListDTO> productListDTOS = productListDAO.find(productListDTO);
        assertThat(productListDTOS.size()).isEqualTo(1);
        assertThat(productListDTOS.get(0).getName()).isEqualTo(productListDTO.getName());
        assertThat(productListDTOS.get(0).getId()).isEqualTo(productListDTO.getId());
        assertThat(productListDTOS.get(0).getProducts().get(0).getId()).isEqualTo(SentinelData.getProduct().getId());
        assertThat(productListDTOS.get(0).getRestrictions().get(0).getValues()).isEqualTo(platFormRestriction.getValues());
        assertThat(productListDTOS.get(0).getAreasOfWork().get(0)).isEqualTo("AREA1");
        assertThat(productListDTOS.get(0).getAreasOfWork().get(1)).isEqualTo("AREA2");
        productListDAO.delete(productListDTO);
        productListDTOS = productListDAO.find(productListDTO);
        assertThat(productListDTOS.size()).isEqualTo(0);
    }
}

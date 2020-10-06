package services.database;

import dev.morphia.query.experimental.filters.Filters;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import model.list.ProductListDTO;
import org.bson.types.ObjectId;
import services.entities.ProductList;
import utils.database.MongoDBManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductListDBDAO implements DAO<ProductListDTO> {

    private static ProductListDBDAO instance;
    private final MongoDBManager database;
    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final ProductDBDAO productDBDAO;

    private ProductListDBDAO() {
        database = MongoDBManager.getMongoDBManager();
        productDBDAO = ProductDBDAO.getInstance();
    }

    public static ProductListDBDAO getInstance() {
        if (instance == null) {
            instance = new ProductListDBDAO();
        }
        return instance;
    }

    @Override
    public List<ProductListDTO> getCollection() {
        return toDAO(database.getDatastore().find(ProductList.class).iterator().toList());
    }

    @Override
    public List<ProductListDTO> find(ProductListDTO dao) {
        return toDAO(database.getDatastore()
                .find(ProductList.class)
                .filter(Filters.eq("id", dao.getId()))
                .iterator()
                .toList());
    }

    public ProductListDTO findByName(ProductListDTO dao) {
        return toDAO(database.getDatastore()
                .find(ProductList.class)
                .filter(Filters.eq("name", dao.getName()))
                .first());
    }

    @Override
    public ProductListDTO findFirst(ProductListDTO dao) {
        return toDAO(database.getDatastore()
                .find(ProductList.class)
                .filter(Filters.eq("id", dao.getId()))
                .first());
    }

    @Override
    public void save(ProductListDTO dao) {
        ProductList save = database.getDatastore().save(toEntity(dao));
        dao.setId(save.getId());
    }

    @Override
    public void delete(ProductListDTO dao) {
        database.getDatastore().delete(toEntity(dao));
    }

    public List<ProductListDTO> toDAO(List<ProductList> toList) {
        if (toList == null)
            return null;
        List<ProductListDTO> result = new ArrayList<>();
        toList.forEach(e->result.add(toDAO(e)));
        return result;
    }

    public ProductListDTO toDAO(ProductList product) {
        if (product == null)
            return null;
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty(), new SimpleStringProperty());
        productListDTO.setName(product.getName());
        productListDTO.setDescription(product.getDescription());
        productListDTO.setProducts(FXCollections.observableList(productDBDAO.toDAO(product.getProducts())));
        productListDTO.setRestrictions(product.getRestrictions());
        productListDTO.setAreasOfWork(FXCollections.observableList(product.getAreasOfWork()));
        productListDTO.setId(product.getId());
        return productListDTO;
    }

    public List<ProductList> toEntity(List<ProductListDTO> productListDTOS) {
        if (productListDTOS == null)
            return null;
        List<ProductList> result = new ArrayList<>();
        productListDTOS.forEach(e->result.add(toEntity(e)));
        return result;
    }

    public ProductList toEntity(ProductListDTO productListDTO) {
        if (productListDTO == null)
            return null;
        ProductList productList = new ProductList();
        productList.setName(productListDTO.getName());
        productList.setDescription(productListDTO.getDescription());
        productList.setProducts(productDBDAO.toEntity(productListDTO.getProducts()));
        productList.setRestrictions(productListDTO.getRestrictions());
        productList.setAreasOfWork(productListDTO.getAreasOfWork());
        if (productListDTO.getProducts().size()>0)
            productListDTO.getProducts().forEach(productDBDAO::save);
        if (productListDTO.getId() == null) {
            ObjectId objectId = new ObjectId();
            productListDTO.setId(objectId);
        }
        productList.setId(productListDTO.getId());

        return productList;
    }

    public void delete(List<ProductListDTO> productList) {
        productList.forEach(this::delete);
    }
}

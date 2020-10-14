package services.database;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import model.products.Sentinel2ProductDTO;
import services.database.mappers.DAOMapper;
import services.database.mappers.SentinelMapper;
import services.entities.Product;
import services.entities.Sentinel1Product;
import services.entities.Sentinel2Product;
import utils.database.MongoDBManager;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ProductDBDAO implements DAO<ProductDTO> {

    private static ProductDBDAO instance;
    private final MongoDBManager database;
    private final DAOMapper<ProductDTO,Product> mapper;

    private ProductDBDAO() {
        database = MongoDBManager.getMongoDBManager();
        mapper = new SentinelMapper();
    }

    public static ProductDBDAO getInstance() {
        if (instance == null) {
            instance = new ProductDBDAO();
        }
        return instance;
    }

    @Override
    public List<ProductDTO> getCollection() {
        return mapper.toDAO(database.getDatastore().find(Product.class).asList());
    }

    @Override
    public List<ProductDTO> find(ProductDTO dao) {
        return mapper.toDAO(database.getDatastore()
                .find(Product.class)
                .field("id")
                .equal(dao.getId())
                .asList());
    }

    @Override
    public ProductDTO findFirst(ProductDTO dao) {
        return mapper.toDAO(database.getDatastore()
                .find(Product.class)
                .field("id")
                .equal(dao.getId())
                .first());
    }

    @Override
    public void save(ProductDTO dao) {
        database.getDatastore().save(mapper.toEntity(dao));
    }

    @Override
    public void delete(ProductDTO dao) {
        database.getDatastore().delete(mapper.toEntity(dao));
    }

    @Override
    public void delete(List<ProductDTO> dao) {
        dao.forEach(this::delete);
    }

    public DAOMapper<ProductDTO,Product> getMapper() {
        return mapper;
    }
}

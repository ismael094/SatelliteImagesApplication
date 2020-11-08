package services.database;

import model.products.ProductDTO;
import services.database.mappers.DAOMapper;
import services.database.mappers.ProductDAOMapper;
import services.entities.Product;
import utils.database.MongoDBManager;

import java.util.List;

public class ProductDBDAO implements DAO<ProductDTO> {

    private static ProductDBDAO instance;
    private final MongoDBManager database;
    private final DAOMapper<ProductDTO,Product> mapper;

    private ProductDBDAO() {
        database = MongoDBManager.getMongoDBManager();
        mapper = new ProductDAOMapper();
    }

    public static ProductDBDAO getInstance() {
        if (instance == null) {
            instance = new ProductDBDAO();
        }
        return instance;
    }

    @Override
    public List<ProductDTO> getCollection() {
        return mapper.toDTO(database.getDatastore().find(Product.class).asList());
    }

    @Override
    public List<ProductDTO> find(ProductDTO dto) {
        return mapper.toDTO(database.getDatastore()
                .find(Product.class)
                .field("id")
                .equal(dto.getId())
                .asList());
    }

    @Override
    public ProductDTO findFirst(ProductDTO dao) {
        return mapper.toDTO(database.getDatastore()
                .find(Product.class)
                .field("id")
                .equal(dao.getId())
                .first());
    }

    @Override
    public void save(ProductDTO dto) {
        database.getDatastore().save(mapper.toEntity(dto));
    }

    @Override
    public void delete(ProductDTO dto) {
        database.getDatastore().delete(mapper.toEntity(dto));
    }

    @Override
    public void delete(List<ProductDTO> dto) {
        dto.forEach(this::delete);
    }

    public DAOMapper<ProductDTO,Product> getMapper() {
        return mapper;
    }
}

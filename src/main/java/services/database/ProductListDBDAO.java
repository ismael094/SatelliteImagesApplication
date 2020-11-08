package services.database;

import dev.morphia.Key;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import model.list.ProductListDTO;
import model.preprocessing.workflow.WorkflowDTO;
import org.bson.types.ObjectId;
import services.database.mappers.WorkflowMapper;
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
    private final WorkflowMapper workflowMapper;

    private ProductListDBDAO() {
        database = MongoDBManager.getMongoDBManager();
        productDBDAO = ProductDBDAO.getInstance();
        workflowMapper = new WorkflowMapper();
    }

    public static ProductListDBDAO getInstance() {
        if (instance == null) {
            instance = new ProductListDBDAO();
        }
        return instance;
    }

    @Override
    public List<ProductListDTO> getCollection() {
        return toDTO(database.getDatastore().find(ProductList.class).asList());
    }

    @Override
    public List<ProductListDTO> find(ProductListDTO dao) {
        return toDTO(database.getDatastore()
                .find(ProductList.class)
                .field("id")
                .equal(dao.getId())
                .asList());
    }

    public ProductListDTO findByName(ProductListDTO dao) {
        return toDTO(database.getDatastore()
                .find(ProductList.class)
                .field("name")
                .equal(dao.getName())
                .first());
    }

    public ProductListDTO findByWorkflow(WorkflowDTO workflowDTO) {
        return toDTO(database.getDatastore()
                .find(ProductList.class)
                .field("workflows")
                .hasThisOne(workflowDTO.getId())
                .first());
    }

    @Override
    public ProductListDTO findFirst(ProductListDTO dao) {
        return toDTO(database.getDatastore()
                .find(ProductList.class)
                .field("id")
                .equal(dao.getId())
                .first());
    }

    @Override
    public void save(ProductListDTO dao) {
        Key<ProductList> save = database.getDatastore().save(toEntity(dao));
        //Save id of the database in the model
        ObjectId id = (ObjectId)save.getId();
        dao.setId(id);
    }

    @Override
    public void delete(ProductListDTO dao) {
        database.getDatastore().delete(toEntity(dao));
    }

    public List<ProductListDTO> toDTO(List<ProductList> toList) {
        if (toList == null)
            return null;
        List<ProductListDTO> result = new ArrayList<>();
        toList.forEach(e->result.add(toDTO(e)));
        return result;
    }

    public ProductListDTO toDTO(ProductList product) {
        if (product == null)
            return null;
        ProductListDTO productListDTO =
                new ProductListDTO(new SimpleStringProperty(), new SimpleStringProperty());

        productListDTO.setName(product.getName());
        productListDTO.setDescription(product.getDescription());
        productListDTO.setProducts(
                FXCollections.observableList(productDBDAO.getMapper().toDTO(product.getProducts())));

        productListDTO.setRestrictions(product.getRestrictions());
        productListDTO.setAreasOfWork(FXCollections.observableList(product.getAreasOfWork()));
        productListDTO.setReferenceProducts(
                FXCollections.observableList(productDBDAO.getMapper().toDTO(product.getReferenceImages())));

        productListDTO.setId(product.getId());
        productListDTO.setWorkflows(workflowMapper.toDTO(product.getWorkflows()));
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
        productList.setProducts(productDBDAO.getMapper().toEntity(productListDTO.getProducts()));
        productList.setReferenceImages(productDBDAO.getMapper().toEntity(productListDTO.getReferenceProducts()));
        productList.setRestrictions(productListDTO.getRestrictions());
        productList.setAreasOfWork(productListDTO.getAreasOfWork());
        productList.setWorkflows(workflowMapper.toEntity(productListDTO.getWorkflows()));
        if (productListDTO.getProducts().size()>0 || productListDTO.getReferenceProducts().size()>0) {
            productListDTO.getProducts().forEach(productDBDAO::save);
            productListDTO.getReferenceProducts().forEach(productDBDAO::save);
        }
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

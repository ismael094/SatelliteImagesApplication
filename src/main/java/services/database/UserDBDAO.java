package services.database;

import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import dev.morphia.query.UpdateResults;
import javafx.collections.FXCollections;
import model.list.ProductListDTO;
import model.processing.WorkflowDTO;
import model.user.UserDTO;
import services.database.mappers.WorkflowMapper;
import services.entities.User;
import utils.Encryptor;
import utils.database.MongoDBManager;

import java.util.ArrayList;
import java.util.List;

public class UserDBDAO implements DAO<UserDTO> {

    private static UserDBDAO instance;
    private final MongoDBManager database;
    private final ProductListDBDAO productListDBDAO;
    private final WorkflowDBDAO workflowDBDAO;

    private UserDBDAO() {
        database = MongoDBManager.getMongoDBManager();
        productListDBDAO = ProductListDBDAO.getInstance();
        workflowDBDAO = WorkflowDBDAO.getInstance();
    }

    public static UserDBDAO getInstance() {
        if (instance == null) {
            instance = new UserDBDAO();
        }
        return instance;
    }

    @Override
    public List<UserDTO> getCollection() {
        return toDAO(database.getDatastore().find(User.class).asList());
    }

    @Override
    public List<UserDTO> find(UserDTO dao) {
        return toDAO(database.getDatastore()
                .find(User.class)
                .field("password")
                .equal(Encryptor.hashString(dao.getPassword()))
                .field("email")
                .equal(dao.getEmail())
                .asList());
    }

    @Override
    public UserDTO findFirst(UserDTO dao) {
        return toDAO(database.getDatastore()
                .find(User.class)
                .field("password")
                .equal(Encryptor.hashString(dao.getPassword()))
                .field("email")
                .equal(dao.getEmail())
                .first());
    }

    public UserDTO findByEmail(UserDTO userDTO) {
        return toDAO(database.getDatastore()
                .find(User.class)
                .field("email")
                .equal(userDTO.getEmail())
                .first());
    }

    @Override
    public void save(UserDTO dao) {
        dao.getProductListsDTO().forEach(productListDBDAO::save);
        dao.getWorkflows().forEach(workflowDBDAO::save);
        database.getDatastore().save(toEntity(dao));
    }

    @Override
    public void delete(UserDTO dao) {
        dao.getProductListsDTO().forEach(productListDBDAO::delete);
        dao.getWorkflows().forEach(workflowDBDAO::delete);
        database.getDatastore().delete(toEntity(dao));
    }

    @Override
    public void delete(List<UserDTO> dao) {
        dao.forEach(this::delete);
    }

    public List<UserDTO> toDAO(List<User> toList) {
        if (toList == null)
            return null;
        List<UserDTO> result = new ArrayList<>();
        toList.forEach(e->result.add(toDAO(e)));
        return result;
    }

    public UserDTO toDAO(User user) {
        if (user == null)
            return null;
        UserDTO userDTO = new UserDTO(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        userDTO.setSearchParameters(user.getSearchParameters());
        userDTO.setWorkflows(FXCollections.observableArrayList(new WorkflowMapper().toDAO(user.getWorkflows())));
        userDTO.setId(user.getId());
        if (user.getProductLists() == null)
            userDTO.setProductListsDTO(FXCollections.observableArrayList());
        else if (user.getProductLists().size()>0)
            userDTO.setProductListsDTO(FXCollections.observableList(productListDBDAO.toDAO(user.getProductLists())));

        return userDTO;
    }

    public User toEntity(UserDTO userDTO) {
        String hashedPass = userDTO.getPassword();
        if (!userDTO.getPassword().startsWith("$2a$10"))
            hashedPass = Encryptor.hashString(userDTO.getPassword());

        User user = new User(userDTO.getId(), userDTO.getEmail(), hashedPass, userDTO.getFirstName(), userDTO.getLastName(), userDTO.getSearchParameters());
        user.setWorkflows(new WorkflowMapper().toEntity(userDTO.getWorkflows()));
        if (userDTO.getProductListsDTO().size()>0) {
            user.setProductLists(productListDBDAO.toEntity(userDTO.getProductListsDTO()));
        }

        return user;
    }

    public void updateProductList(UserDTO user) {
        user.getProductListsDTO().forEach(pL->{
            if (productListDBDAO.findByName(pL) == null)
                productListDBDAO.save(pL);
        });
        //save(user);
        Query<User> email = database.getDatastore().find(User.class)
                .field("email")
                .equal(user.getEmail());
        UpdateOperations<User> ops = database.getDatastore()
                .createUpdateOperations(User.class)
                .set("productLists", productListDBDAO.toEntity(user.getProductListsDTO()));

        UpdateResults update = database.getDatastore().update(email, ops);
        System.out.println(update.getUpdatedCount());
        ;
        /*database.getDatastore().find(User.class)
                .filter(Filters.eq("email", user.getEmail()))
                .update(UpdateOperators.set("productLists", productListDBDAO.toEntity(user.getProductListsDTO())))
                .execute();*/
    }

    public void addProductList(UserDTO user, ProductListDTO productListDTO) {
        productListDBDAO.save(productListDTO);
        //save(user);
        Query<User> email = database.getDatastore().find(User.class)
                .field("email")
                .equal(user.getEmail());
        UpdateOperations<User> ops = database.getDatastore()
                .createUpdateOperations(User.class)
                .push("productLists", productListDBDAO.toEntity(productListDTO));

        UpdateResults update = database.getDatastore().update(email, ops);
        System.out.println(update.getUpdatedCount());
        ;
        /*database.getDatastore().find(User.class)
                .filter(Filters.eq("email", user.getEmail()))
                .update(UpdateOperators.set("productLists", productListDBDAO.toEntity(user.getProductListsDTO())))
                .execute();*/
    }

    public void removeProductList(UserDTO user, ProductListDTO productListDTO) {
        //user.getProductListsDTO().forEach(productListDBDAO::save);
        //save(user);
        Query<User> email = database.getDatastore().find(User.class)
                .field("email")
                .equal(user.getEmail());
        UpdateOperations<User> ops = database.getDatastore()
                .createUpdateOperations(User.class)
                .removeAll("productLists", productListDBDAO.toEntity(productListDTO));

        UpdateResults update = database.getDatastore().update(email, ops);
        System.out.println(update.getUpdatedCount());
        ;
        /*database.getDatastore().find(User.class)
                .filter(Filters.eq("email", user.getEmail()))
                .update(UpdateOperators.set("productLists", productListDBDAO.toEntity(user.getProductListsDTO())))
                .execute();*/
    }

    public void addWorkflow(UserDTO user, WorkflowDTO workflowDTO) {
        workflowDBDAO.save(workflowDTO);
        //save(user);
        Query<User> email = database.getDatastore().find(User.class)
                .field("email")
                .equal(user.getEmail());
        UpdateOperations<User> ops = database.getDatastore()
                .createUpdateOperations(User.class)
                .push("workflows", new WorkflowMapper().toEntity(workflowDTO));

        UpdateResults update = database.getDatastore().update(email, ops);
        System.out.println(update.getUpdatedCount());
        ;
        /*database.getDatastore().find(User.class)
                .filter(Filters.eq("email", user.getEmail()))
                .update(UpdateOperators.set("productLists", productListDBDAO.toEntity(user.getProductListsDTO())))
                .execute();*/
    }

    public void removeWorkflow(UserDTO user, WorkflowDTO workflowDTO) {
        //user.getProductListsDTO().forEach(productListDBDAO::save);
        //save(user);
        Query<User> email = database.getDatastore().find(User.class)
                .field("email")
                .equal(user.getEmail());
        UpdateOperations<User> ops = database.getDatastore()
                .createUpdateOperations(User.class)
                .removeAll("workflows", new WorkflowMapper().toEntity(workflowDTO));

        UpdateResults update = database.getDatastore().update(email, ops);
        System.out.println(update.getUpdatedCount());
        ;
        /*database.getDatastore().find(User.class)
                .filter(Filters.eq("email", user.getEmail()))
                .update(UpdateOperators.set("productLists", productListDBDAO.toEntity(user.getProductListsDTO())))
                .execute();*/
    }

    public void updateWorkflow(UserDTO user) {
        //user.getProductListsDTO().forEach(productListDBDAO::save);
        //save(user);
        Query<User> email = database.getDatastore().find(User.class)
                .field("email")
                .equal(user.getEmail());
        UpdateOperations<User> ops = database.getDatastore()
                .createUpdateOperations(User.class)
                .set("workflows", new WorkflowMapper().toEntity(user.getWorkflows()));

        UpdateResults update = database.getDatastore().update(email, ops);
        System.out.println(update.getUpdatedCount());
        ;
        /*database.getDatastore().find(User.class)
                .filter(Filters.eq("email", user.getEmail()))
                .update(UpdateOperators.set("productLists", productListDBDAO.toEntity(user.getProductListsDTO())))
                .execute();*/
    }
}

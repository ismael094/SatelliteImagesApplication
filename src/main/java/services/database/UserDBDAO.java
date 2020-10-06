package services.database;


import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import javafx.collections.FXCollections;
import model.user.UserDTO;
import services.entities.User;
import utils.Encryptor;
import utils.database.MongoDBManager;

import java.util.ArrayList;
import java.util.List;

public class UserDBDAO implements DAO<UserDTO> {

    private static UserDBDAO instance;
    private final MongoDBManager database;
    private final ProductListDBDAO productListDBDAO;

    private UserDBDAO() {
        database = MongoDBManager.getMongoDBManager();
        productListDBDAO = ProductListDBDAO.getInstance();
    }

    public static UserDBDAO getInstance() {
        if (instance == null) {
            instance = new UserDBDAO();
        }
        return instance;
    }

    @Override
    public List<UserDTO> getCollection() {
        return toDAO(database.getDatastore().find(User.class).iterator().toList());
    }

    @Override
    public List<UserDTO> find(UserDTO dao) {
        return toDAO(database.getDatastore()
                .find(User.class)
                .filter(Filters.eq("password", Encryptor.hashString(dao.getPassword())),Filters.eq("email",dao.getEmail()))
                .iterator()
                .toList());
    }

    @Override
    public UserDTO findFirst(UserDTO dao) {
        return toDAO(database.getDatastore()
                .find(User.class)
                .filter(Filters.eq("password",Encryptor.hashString(dao.getPassword())),Filters.eq("email",dao.getEmail()))
                .first());
    }

    public UserDTO findByEmail(UserDTO userDTO) {
        return toDAO(database.getDatastore().find(User.class).filter(Filters.eq("email", userDTO.getEmail())).first());
    }

    @Override
    public void save(UserDTO dao) {
        database.getDatastore().save(toEntity(dao));
    }

    @Override
    public void delete(UserDTO dao) {
        database.getDatastore().delete(toEntity(dao));
        dao.getProductListsDTO().forEach(productListDBDAO::delete);
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
        User user = new User(userDTO.getId(), userDTO.getEmail(), hashedPass, userDTO.getFirstName(), userDTO.getLastName());

        if (userDTO.getProductListsDTO().size()>0) {
            user.setProductLists(productListDBDAO.toEntity(userDTO.getProductListsDTO()));
            userDTO.getProductListsDTO().forEach(pL->{
                if (productListDBDAO.findByName(pL) == null)
                    productListDBDAO.save(pL);
            });
        }

        return user;
    }

    public void updateProductList(UserDTO user) {
        user.getProductListsDTO().forEach(pL->{
            if (productListDBDAO.findByName(pL) == null)
                productListDBDAO.save(pL);
        });
        database.getDatastore().find(User.class)
                .filter(Filters.eq("email", user.getEmail()))
                .update(UpdateOperators.set("productLists", productListDBDAO.toEntity(user.getProductListsDTO())))
                .execute();
    }
}

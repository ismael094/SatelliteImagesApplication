package services.database;

import dev.morphia.query.experimental.filters.Filters;
import javafx.beans.property.SimpleStringProperty;
import model.SentinelData;
import model.list.ProductListDTO;
import model.user.UserDTO;
import org.junit.Before;
import org.junit.Test;
import utils.MongoDB_;
import services.entities.User;
import utils.Encryptor;
import utils.database.MongoDBManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDBDAO_ {
    private UserDBDAO userDAO;
    private MongoDBManager mongodb;
    private UserDTO userDTO;

    @Before
    public void init() {
        mongodb = MongoDBManager.getMongoDBManager();
        if (!mongodb.isConnected()) {
            mongodb.setCredentialsAndDatabase(MongoDB_.USER,MongoDB_.PASSWORD,MongoDB_.DATABASE);
            mongodb.connect();
        }
        userDAO = UserDBDAO.getInstance();
        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty());
    }

    @Test
    public void get_user_collection() {
        List<UserDTO> usersDTO = userDAO.getCollection();
        assertThat(usersDTO.size()).isGreaterThan(0);
        assertThat(usersDTO.get(0)).isNotNull();
    }

    @Test
    public void find_user_by_email() {
        userDTO.setEmail("a@a.com");
        UserDTO dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getEmail()).isEqualTo(userDTO.getEmail());

    }

    @Test
    public void save_and_delete_user_collection() {
        userDTO.setEmail("email@mail.com");
        userDTO.setPassword("password");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        userDAO.save(userDTO);
        UserDTO dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getEmail()).isEqualTo(userDTO.getEmail());
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void save_and_delete_user_collection_with_product_list() {
        userDTO.setEmail("email@mail.com");
        userDTO.setPassword("password");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getProduct());
        userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);
        UserDTO dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(dbUserDTO.getProductListsDTO().size()).isEqualTo(1);
        assertThat(dbUserDTO.getProductListsDTO().get(0).getName()).isEqualTo(productListDTO.getName());
        assertThat(dbUserDTO.getProductListsDTO().get(0).getProducts().get(0).getId()).isEqualTo(productListDTO.getProducts().get(0).getId());
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void update_user_collection_with_product_list() {
        userDTO.setEmail("email@mail.com");
        userDTO.setPassword("password");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getProduct());
        //userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);
        UserDTO dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNotNull();
        userDTO.addProductList(productListDTO);
        userDAO.updateProductList(userDTO);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO.getProductListsDTO()).isNotNull();
        assertThat(dbUserDTO.getProductListsDTO().size()).isEqualTo(1);
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void userDTO_to_entity() {
        UserDTO userDTO = new UserDTO("email@mail.is","pass","firstName","lastName");
        User user = userDAO.toEntity(userDTO);
        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(Encryptor.matchString ("pass", user.getPassword())).isTrue();
        assertThat(userDTO.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(userDTO.getLastName()).isEqualTo(user.getLastName());
    }

    @Test
    public void entity_to_UserDTO() {
        User user = mongodb.getDatastore().find(User.class).filter(Filters.eq("email", "a@a.com")).first();
        UserDTO userDTO = userDAO.toDAO(user);
        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDTO.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDTO.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(userDTO.getLastName()).isEqualTo(user.getLastName());
        assertThat(userDTO.getId()).isEqualTo(user.getId());
    }
}


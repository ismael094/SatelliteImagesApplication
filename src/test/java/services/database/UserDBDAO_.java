package services.database;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.SentinelData;
import model.list.ProductListDTO;
import model.processing.workflow.GeneralWorkflowDTO;
import model.processing.workflow.operation.Operator;
import model.processing.workflow.Sentinel1GRDDefaultWorkflowDTO;
import model.processing.workflow.WorkflowType;
import model.user.UserDTO;
import org.junit.Before;
import org.junit.Test;
import utils.database.MongoDB_;
import services.entities.User;
import utils.Encryptor;
import utils.database.MongoDBManager;

import java.util.List;

import static java.lang.System.currentTimeMillis;
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
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name2"), new SimpleStringProperty("Description"));
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
    public void save_and_delete_user_collection_with_product_list_and_workflow() {
        userDTO.setEmail("email@mail.com");
        userDTO.setPassword("password");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        Sentinel1GRDDefaultWorkflowDTO workflowDTO = new Sentinel1GRDDefaultWorkflowDTO();
        userDTO.addWorkflow(workflowDTO);
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name2"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getProduct());

        productListDTO.addWorkflow(workflowDTO);

        userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);

        UserDTO dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(dbUserDTO.getProductListsDTO().size()).isEqualTo(1);
        assertThat(dbUserDTO.getProductListsDTO().get(0).getName()).isEqualTo(productListDTO.getName());
        assertThat(dbUserDTO.getProductListsDTO().get(0).getProducts().get(0).getId()).isEqualTo(productListDTO.getProducts().get(0).getId());
        assertThat(dbUserDTO.getWorkflows().size()).isNotNull();
        assertThat(dbUserDTO.getWorkflows().get(0).getType()).isInstanceOf(WorkflowType.class);
        assertThat(dbUserDTO.getWorkflows().get(0).getOperation(Operator.TERRAIN_CORRECTION).getParameters()).isNotNull();
        assertThat(dbUserDTO.getWorkflows().get(0).getOperation(Operator.TERRAIN_CORRECTION).getParameters().get("sourceBands")).isEqualTo("Beta0_VH,Beta0_VV");
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void add_and_remove_workflow() {
        userDTO.setEmail("email@mail.com");
        userDTO.setPassword("password");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        userDTO.addWorkflow(new Sentinel1GRDDefaultWorkflowDTO());
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name2"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getProduct());

        userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);

        UserDTO dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getWorkflows().size()).isNotNull();
        assertThat(dbUserDTO.getWorkflows().get(0).getType()).isInstanceOf(WorkflowType.class);
        assertThat(dbUserDTO.getWorkflows().get(0).getId()).isEqualTo(userDTO.getWorkflows().get(0).getId());
        GeneralWorkflowDTO prueba = new GeneralWorkflowDTO(new SimpleStringProperty("Prueba"), new SimpleObjectProperty<>(WorkflowType.SLC));

        userDAO.addNewWorkflow(userDTO,prueba);
        userDAO.addNewWorkflow(userDTO,prueba);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(2);
        userDAO.removeWorkflow(userDTO,prueba);

        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(1);
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void update_workflow() {
        userDTO.setEmail("email@mail.com");
        userDTO.setPassword("password");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        userDTO.addWorkflow(new Sentinel1GRDDefaultWorkflowDTO());
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name2"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getProduct());
        userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);

        UserDTO dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(1);

        GeneralWorkflowDTO test = new GeneralWorkflowDTO(new SimpleStringProperty("Prueba"), new SimpleObjectProperty<>(WorkflowType.SLC));
        test.setType(WorkflowType.GRD);
        userDTO.addWorkflow(test);
        userDAO.addNewWorkflow(userDTO,test);

        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(2);
        assertThat(dbUserDTO.getWorkflows().get(1).getType()).isEqualTo(WorkflowType.GRD);
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNull();

    }

    @Test
    public void update_user_collection_with_product_list() {
        long start = currentTimeMillis();
        userDTO.setEmail("email@mail.com");
        userDTO.setPassword("password");
        userDTO.setFirstName("firstName");
        userDTO.setLastName("lastName");
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name5"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getProduct());
        //userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);
        UserDTO dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getProductListsDTO().size()).isEqualTo(0);
        userDTO.addProductList(productListDTO);
        userDAO.addProductList(userDTO, productListDTO);
        assertThat(ProductListDBDAO.getInstance().find(productListDTO).size()).isEqualTo(1);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO.getProductListsDTO()).isNotNull();
        assertThat(dbUserDTO.getProductListsDTO().size()).isEqualTo(1);
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByEmail(userDTO);
        assertThat(dbUserDTO).isNull();
        assertThat(currentTimeMillis()-start).isLessThan(4000);
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
        /*User user = mongodb.getDatastore().find(User.class).filter(Filters.eq("email", "a@a.com")).first();
        UserDTO userDTO = userDAO.toDAO(user);
        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDTO.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDTO.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(userDTO.getLastName()).isEqualTo(user.getLastName());
        assertThat(userDTO.getId()).isEqualTo(user.getId());*/
    }
}



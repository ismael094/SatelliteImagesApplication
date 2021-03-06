package services.database;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.SentinelData;
import model.list.ProductListDTO;
import model.preprocessing.workflow.GeneralWorkflowDTO;
import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.operation.Operator;
import model.preprocessing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import model.preprocessing.workflow.WorkflowType;
import model.user.UserDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.database.MongoDBTest;
import services.entities.User;
import utils.Encryptor;
import utils.database.MongoDBManager;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;

public class UserDBDAOTest {
    private UserDBDAO userDAO;
    private WorkflowDBDAO workflowDBDAO;
    private MongoDBManager mongodb;
    private UserDTO userDTO;

    @Before
    public void init() {
        mongodb = MongoDBManager.getMongoDBManager();
        if (!mongodb.isConnected()) {
            mongodb.setCredentialsAndDatabase(MongoDBTest.USER, MongoDBTest.PASSWORD, MongoDBTest.DATABASE);
            mongodb.connect();
        }
        userDAO = UserDBDAO.getInstance();
        workflowDBDAO = WorkflowDBDAO.getInstance();
        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty());
    }

    @After
    public void delete() {
        userDAO.delete(userDTO);
    }

    @Test
    public void get_user_collection() {
        List<UserDTO> usersDTO = userDAO.getCollection();
        assertThat(usersDTO.size()).isGreaterThan(0);
        assertThat(usersDTO.get(0)).isNotNull();
    }

    @Test
    public void find_user_by_username() {
        userDTO.setUsername("pepe98");
        userDTO.setPassword("password");
        userDAO.save(userDTO);
        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(dbUserDTO.getPassword()).isNotEmpty();
        userDAO.delete(userDTO);

    }

    @Test
    public void find_user_not_in_database_by_email() {
        userDTO.setUsername("pepe98");
        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNull();

    }

    @Test
    public void save_and_delete_user_collection() {
        userDTO.setPassword("password");
        userDTO.setUsername("firstName");
        userDAO.save(userDTO);
        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getUsername()).isEqualTo(userDTO.getUsername());
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void save_and_delete_user_collection_with_product_list() {
        userDTO.setPassword("password");
        userDTO.setUsername("firstName");
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name2"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getSentinel1Product());

        userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);

        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(dbUserDTO.getProductListsDTO().size()).isEqualTo(1);
        assertThat(dbUserDTO.getProductListsDTO().get(0).getName()).isEqualTo(productListDTO.getName());
        assertThat(dbUserDTO.getProductListsDTO().get(0).getProducts().get(0).getId()).isEqualTo(productListDTO.getProducts().get(0).getId());
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void save_and_delete_user_collection_with_product_list_and_workflow() {
        userDTO.setPassword("password");
        userDTO.setUsername("firstName");
        GRDDefaultWorkflowDTO workflowDTO = new GRDDefaultWorkflowDTO();
        userDTO.addWorkflow(workflowDTO);
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name2"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getSentinel1Product());

        productListDTO.addWorkflow(workflowDTO);

        userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);

        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(dbUserDTO.getProductListsDTO().size()).isEqualTo(1);
        assertThat(dbUserDTO.getProductListsDTO().get(0).getName()).isEqualTo(productListDTO.getName());
        assertThat(dbUserDTO.getProductListsDTO().get(0).getProducts().get(0).getId()).isEqualTo(productListDTO.getProducts().get(0).getId());
        assertThat(dbUserDTO.getWorkflows().size()).isNotNull();
        assertThat(dbUserDTO.getWorkflows().get(0).getType()).isInstanceOf(WorkflowType.class);
        assertThat(dbUserDTO.getWorkflows().get(0).getOperation(Operator.TERRAIN_CORRECTION).getParameters()).isNotNull();
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void add_and_remove_workflow() {
        userDTO.setPassword("password");
        userDTO.setUsername("firstName");
        userDTO.addWorkflow(new GRDDefaultWorkflowDTO());

        userDAO.save(userDTO);
        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);

        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getWorkflows().size()).isNotNull();
        assertThat(dbUserDTO.getWorkflows().get(0).getType()).isInstanceOf(WorkflowType.class);
        assertThat(dbUserDTO.getWorkflows().get(0).getId()).isEqualTo(userDTO.getWorkflows().get(0).getId());

        GeneralWorkflowDTO prueba = new GeneralWorkflowDTO(new SimpleStringProperty("Prueba"), new SimpleObjectProperty<>(WorkflowType.SLC));

        userDAO.addNewWorkflow(userDTO,prueba);
        userDAO.addNewWorkflow(userDTO,prueba);

        assertThat(workflowDBDAO.find(prueba)).isNotNull();

        dbUserDTO = userDAO.findByUsername(userDTO);

        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(2);

        assertThat(workflowDBDAO.find(dbUserDTO.getWorkflows().get(0))).isNotNull();
        assertThat(workflowDBDAO.find(dbUserDTO.getWorkflows().get(1))).isNotNull();

        userDAO.removeWorkflow(userDTO,prueba);

        dbUserDTO = userDAO.findByUsername(userDTO);

        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(1);

        WorkflowDTO workflowDTOS = workflowDBDAO.findFirst(prueba);

        assertThat(workflowDTOS).isNull();

        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(1);

        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void add_and_remove_workflow_with_product_list() {
        userDTO.setPassword("password");
        userDTO.setUsername("firstName");
        userDTO.addWorkflow(new GRDDefaultWorkflowDTO());
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name2"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getSentinel1Product());

        userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);

        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getWorkflows().size()).isNotNull();
        assertThat(dbUserDTO.getWorkflows().get(0).getType()).isInstanceOf(WorkflowType.class);
        assertThat(dbUserDTO.getWorkflows().get(0).getId()).isEqualTo(userDTO.getWorkflows().get(0).getId());
        GeneralWorkflowDTO prueba = new GeneralWorkflowDTO(new SimpleStringProperty("Prueba"), new SimpleObjectProperty<>(WorkflowType.SLC));
        productListDTO.addWorkflow(prueba);
        userDAO.updateProductList(userDTO);

        userDAO.addNewWorkflow(userDTO,prueba);
        userDAO.addNewWorkflow(userDTO,prueba);
        assertThat(workflowDBDAO.find(prueba)).isNotNull();

        dbUserDTO = userDAO.findByUsername(userDTO);

        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(2);
        assertThat(workflowDBDAO.find(dbUserDTO.getWorkflows().get(0))).isNotNull();

        userDAO.removeWorkflow(userDTO,prueba);

        WorkflowDTO workflowDTOS = workflowDBDAO.findFirst(prueba);


        assertThat(workflowDTOS).isNull();

        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(1);
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNull();
    }

    @Test
    public void update_workflow() {
        userDTO.setPassword("password");
        userDTO.setUsername("firstName");
        userDTO.addWorkflow(new GRDDefaultWorkflowDTO());
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name2"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getSentinel1Product());
        userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);

        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(1);

        GeneralWorkflowDTO test = new GeneralWorkflowDTO(new SimpleStringProperty("Prueba"), new SimpleObjectProperty<>(WorkflowType.SLC));
        test.setType(WorkflowType.GRD);
        userDTO.addWorkflow(test);
        userDAO.addNewWorkflow(userDTO,test);

        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO.getWorkflows().size()).isEqualTo(2);
        assertThat(dbUserDTO.getWorkflows().get(1).getType()).isEqualTo(WorkflowType.GRD);
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNull();

    }

    @Test
    public void update_user_collection_with_product_list() {
        long start = currentTimeMillis();
        userDTO.setPassword("password");
        userDTO.setUsername("firstName");
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name5"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getSentinel1Product());
        //userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);
        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(dbUserDTO.getProductListsDTO().size()).isEqualTo(0);
        userDTO.addProductList(productListDTO);
        userDAO.addProductList(userDTO, productListDTO);
        assertThat(ProductListDBDAO.getInstance().find(productListDTO).size()).isEqualTo(1);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO.getProductListsDTO()).isNotNull();
        assertThat(dbUserDTO.getProductListsDTO().size()).isEqualTo(1);
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNull();
        assertThat(currentTimeMillis()-start).isLessThan(4000);
    }

    @Test
    public void update_user() {
        long start = currentTimeMillis();
        userDTO.setPassword("password");
        userDTO.setUsername("firstName");
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("name5"), new SimpleStringProperty("Description"));
        productListDTO.addProduct(SentinelData.getProduct());
        //userDTO.addProductList(productListDTO);
        userDAO.save(userDTO);
        UserDTO dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(Encryptor.matchString(userDTO.getPassword(),dbUserDTO.getPassword())).isTrue();

        userDTO.setPassword("new_password");
        userDAO.save(userDTO);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNotNull();
        assertThat(Encryptor.matchString(userDTO.getPassword(),dbUserDTO.getPassword())).isTrue();
        userDAO.delete(dbUserDTO);
        dbUserDTO = userDAO.findByUsername(userDTO);
        assertThat(dbUserDTO).isNull();
        assertThat(currentTimeMillis()-start).isLessThan(4000);
    }

    @Test
    public void userDTO_to_entity() {
        UserDTO userDTO = new UserDTO("pass","firstName");
        User user = userDAO.toEntity(userDTO);
        assertThat(userDTO.getUsername()).isEqualTo(user.getUsername());
        assertThat(Encryptor.matchString ("pass", user.getPassword())).isTrue();
        assertThat(userDTO.getUsername()).isEqualTo(user.getUsername());
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



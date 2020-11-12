package model.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.list.ProductListDTO;
import model.preprocessing.workflow.WorkflowDTO;
import org.bson.types.ObjectId;


public class UserDTO {
    private ObjectId id;
    private StringProperty password;
    private StringProperty username;
    private ObservableList<ProductListDTO> productListsDTO;
    private ObservableList<WorkflowDTO> workflows;

    public UserDTO() {
        this.productListsDTO = FXCollections.observableArrayList();
    }

    public UserDTO(StringProperty password, StringProperty username) {
        this.password = password;
        this.username = username;
        this.id = null;
        this.productListsDTO = FXCollections.observableArrayList();
        this.workflows = FXCollections.observableArrayList();
    }

    public UserDTO(String password, String username) {
        this.password = new SimpleStringProperty(password);
        this.username = new SimpleStringProperty(username);
        this.productListsDTO = FXCollections.observableArrayList();
        this.workflows = FXCollections.observableArrayList();
    }

    public void addProductList(ProductListDTO productListDTO) {
        productListsDTO.add(productListDTO);
    }

    public ObservableList<ProductListDTO> getProductListsDTO() {
        return productListsDTO;
    }

    public void setProductListsDTO(ObservableList<ProductListDTO> productListsDTO) {
        this.productListsDTO = productListsDTO;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String firstName) {
        this.username.set(firstName);
    }

    public ObservableList<WorkflowDTO> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(ObservableList<WorkflowDTO> workflows) {
        this.workflows = workflows;
    }

    public void addWorkflow(WorkflowDTO workflow) {
        this.workflows.add(workflow);
    }

    public void removeWorkflow(WorkflowDTO workflow) {
        this.workflows.remove(workflow);
    }
}

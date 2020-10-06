package model.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.list.ProductListDTO;
import org.bson.types.ObjectId;

public class UserDTO {
    private ObjectId id;
    private StringProperty email;
    private StringProperty password;
    private StringProperty firstName;
    private StringProperty lastName;
    private ObservableList<ProductListDTO> productListsDTO;

    public UserDTO() {
        this.productListsDTO = FXCollections.observableArrayList();
    }

    public UserDTO(StringProperty email, StringProperty password, StringProperty firstName, StringProperty lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = null;
        this.productListsDTO = FXCollections.observableArrayList();
    }

    public UserDTO(String email, String password, String firstName, String lastName) {
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.productListsDTO = FXCollections.observableArrayList();
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

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
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

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }
}

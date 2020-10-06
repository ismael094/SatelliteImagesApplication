package services.entities;

import dev.morphia.annotations.*;
import javafx.collections.ObservableList;
import model.list.ProductListDTO;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Entity("users")
@Indexes(
        @Index(fields = @Field("email"))
)
public class User {
    @Id
    private ObjectId id;
    private String email;
    private String password;
    private String lastName;
    private String firstName;
    @Reference
    private List<ProductList> productLists;

    public User(ObjectId id, String email, String password, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.productLists = new ArrayList<>();
    }

    public User() {
        this.productLists = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<ProductList> getProductLists() {
        return productLists;
    }

    public void setProductLists(List<ProductList> productLists) {
        this.productLists = productLists;
    }
}

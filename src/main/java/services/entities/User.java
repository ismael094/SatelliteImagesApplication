package services.entities;

import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity("users")
@Indexes(
        @Index(fields = @Field("email"))
)
public class User {
    @Id
    private ObjectId id;
    private String password;
    private String username;
    @Reference(idOnly = true, ignoreMissing=true,lazy = true)
    private List<ProductList> productLists;
    @Reference(idOnly = true, ignoreMissing=true,lazy = true)
    private List<Workflow> workflows;

    public User(ObjectId id, String password, String username) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.productLists = new ArrayList<>();
        this.workflows = new ArrayList<>();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ProductList> getProductLists() {
        return productLists;
    }

    public void setProductLists(List<ProductList> productLists) {
        this.productLists = productLists;
    }

    public List<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows;
    }
}

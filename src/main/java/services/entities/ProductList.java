package services.entities;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import model.products.ProductDTO;
import model.restriction.Restriction;
import org.bson.types.ObjectId;

import java.util.*;

@Entity("product_lists")
public class ProductList {
    @Id
    private ObjectId id;
    private String name;
    private String description;
    @Reference(idOnly = true, ignoreMissing=true)
    private List<Product> products;
    private List<Restriction> restrictions;
    private List<String> areasOfWork;
    @Reference(idOnly = true, ignoreMissing=true)
    private List<Product> groundTruthProducts;

    public ProductList() {
        products = new ArrayList<>();
        restrictions = new ArrayList<>();
        areasOfWork = new ArrayList<>();
        groundTruthProducts = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<Restriction> restrictions) {
        this.restrictions = restrictions;
    }

    public List<String> getAreasOfWork() {
        return areasOfWork;
    }

    public void setAreasOfWork(List<String> areasOfWork) {
        this.areasOfWork = areasOfWork;
    }

    public void setGroundTruthProducts(List<Product> groundTruthProducts) {
        this.groundTruthProducts = groundTruthProducts;
    }

    public List<Product> getGroundTruthProducts() {
        return groundTruthProducts;
    }
}

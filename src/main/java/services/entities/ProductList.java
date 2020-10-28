package services.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
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
    private List<Product> referenceImages;
    @Reference(idOnly = true, ignoreMissing=true)
    private List<Workflow> workflows;

    public ProductList() {
        products = new ArrayList<>();
        restrictions = new ArrayList<>();
        areasOfWork = new ArrayList<>();
        referenceImages = new ArrayList<>();
        workflows = new ArrayList<>();
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

    public void setReferenceImages(List<Product> referenceImages) {
        this.referenceImages = referenceImages;
    }

    public List<Product> getReferenceImages() {
        return referenceImages;
    }

    public List<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows;
    }
}

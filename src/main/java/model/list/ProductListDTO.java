package model.list;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.WorkflowType;
import model.products.ProductDTO;
import model.restriction.Restriction;
import org.bson.types.ObjectId;
import org.locationtech.jts.io.ParseException;
import services.database.ProductListDBDAO;
import utils.WKTUtil;

import java.util.*;

public class ProductListDTO {
    private ObjectId id;
    private StringProperty name;
    private StringProperty description;
    private ObservableList<ProductDTO> products;
    private final DoubleProperty sizeAsDouble;
    private final IntegerProperty count;
    private List<Restriction> restrictions;
    private ObservableList<String> areasOfWork;
    private ObservableList<ProductDTO> referenceProducts;
    private ObservableList<WorkflowDTO> workflows;

    public ProductListDTO(StringProperty name, StringProperty description) {
        this.name = name;
        this.description = description;
        this.products = FXCollections.observableArrayList();
        this.sizeAsDouble = new SimpleDoubleProperty();
        this.count  = new SimpleIntegerProperty();
        this.restrictions = new ArrayList<>();
        this.areasOfWork = FXCollections.observableArrayList();
        this.referenceProducts = FXCollections.observableArrayList();
        this.workflows = FXCollections.observableArrayList();
    }

    public ProductListDTO() {
        this.products = FXCollections.observableArrayList();
        this.sizeAsDouble = new SimpleDoubleProperty();
        this.count  = new SimpleIntegerProperty();
        this.restrictions = new ArrayList<>();
        this.areasOfWork = FXCollections.observableArrayList();
        this.referenceProducts = FXCollections.observableArrayList();
    }


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setProducts(ObservableList<ProductDTO> products) {
        this.products = products;
        reloadProperties();
    }

    public void setRestrictions(List<Restriction> restrictions) {
        this.restrictions = restrictions;
    }

    public void setAreasOfWork(ObservableList<String> areasOfWork) {
        this.areasOfWork = areasOfWork;
    }

    public ObservableList<ProductDTO> getProducts() {
        return products;
    }

    public ProductDTO getProductById(String id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public int count() {
        return products.size();
    }

    public boolean addProduct(ProductDTO product) {
        if (product == null || products.contains(product) || !validate(product))
            return false;
        this.products.add(product);
        reloadProperties();
        return true;
    }

    public boolean addProduct(List<ProductDTO> products) {
        products.forEach(p->{
            if (!this.products.contains(p) && validate(p))
                this.products.add(p);
        });
        //Reload sizes property
        reloadProperties();
        return true;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    @Override
    public String toString() {
        return name.getValue();
    }

    public void remove(ProductDTO product) {
        products.remove(product);
        reloadProperties();
    }

    public void remove(Collection<ProductDTO> product) {
        products.removeAll(product);
        reloadProperties();
    }

    private void reloadProperties() {
        sizeAsDouble.setValue(productSize());
        count.setValue(products.size());
    }

    public double productSize() {
        return getProducts().stream()
                .mapToDouble(ProductDTO::getSizeAsDouble)
                .sum();
    }

    public double getSizeAsDouble() {
        return sizeAsDouble.get();
    }

    public DoubleProperty sizeAsDoubleProperty() {
        return sizeAsDouble;
    }

    /**
     * Set size property
     * @param sizeAsDouble size
     */
    public void setSizeAsDouble(double sizeAsDouble) {
        this.sizeAsDouble.set(sizeAsDouble);
    }
    /**
     * get count
     * @return number of products
     */
    public int getCount() {
        return count.get();
    }

    /**
     * get count property
     * @return count property
     */
    public IntegerProperty countProperty() {
        return count;
    }

    /**
     * Set number of products
     * @param count number of products in the list
     */
    public void setCount(int count) {
        this.count.set(count);
    }

    /**
     * Add restriction to list
     * @param restriction restriction
     */
    public void addRestriction(Restriction restriction) {
        restrictions.add(restriction);
        List<ProductDTO> productDTOS = new ArrayList<>();
        products.forEach(e->{
            if (!validate(e))
                productDTOS.add(e);
        });
        products.removeAll(productDTOS);
    }

    /**
     * Check all products verify restrictions
     * @param product product
     * @return true if match the restricions; false otherwise
     */
    private boolean validate(ProductDTO product) {
        if (restrictions.size() == 0)
            return true;
        for (Restriction r : restrictions)
            if (!r.validate(product))
                return false;
        return true;
    }

    /**
     * Get all areas of work
     * @return areas of work
     */
    public ObservableList<String> getAreasOfWork() {
        return areasOfWork;
    }

    /**
     * Get valid products of the list. A valid products has minimum one area of work assigned
     * @return List of products with one or more areas of work assigned
     */
    public List<ProductDTO> getValidProducts() {
        List<ProductDTO> res = new ArrayList<>();
        products.forEach(p->{
            List<String> strings = areasOfWorkOfProduct(p.getFootprint());
            if (strings != null && !strings.isEmpty())
                res.add(p);
        });
        return res;
    }

    /**
     * Get valid products and his areas of work
     * @return Map with product as key
     */
    public Map<ProductDTO,List<String>> getProductsAreasOfWorks() {
        Map<ProductDTO, List<String>> areas = new HashMap<>();
        products.forEach(p->{
            List<String> strings = areasOfWorkOfProduct(p.getFootprint());
            if (!strings.isEmpty())
                areas.put(p, areasOfWorkOfProduct(p.getFootprint()));
        });
        return areas;
    }

    /**
     * Areas of work of a product
     * @param footprint Footprint of product
     * @return areas of work intersecting product footprint
     */
    public List<String> areasOfWorkOfProduct(String footprint) {
        if (areasOfWork.size() == 0)
            return null;
        List<String> areas = new ArrayList<>();
        areasOfWork.forEach(a->{
            try {
                if (WKTUtil.workingAreaIntersects(footprint,a))
                    areas.add(a);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return areas;
    }

    /**
     * add new area of work
     * @param area area
     */
    public void addAreaOfWork(String area) {
        if (!areasOfWork.contains(area)){
            this.areasOfWork.add(area);
        }
    }

    /**
     * remove area of work
     * @param area area of work
     */
    public void removeAreaOfWork(String area) {
        this.areasOfWork.remove(area);
    }

    /**
     * Get product list restrictions
     * @return list of restrictions
     */
    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    /**
     * Get all reference images
     * @return List of reference images
     */
    public ObservableList<ProductDTO> getReferenceProducts() {
        return referenceProducts;
    }

    /**
     * Set references images
     * @param referenceProducts reference images
     */
    public void setReferenceProducts(ObservableList<ProductDTO> referenceProducts) {
        this.referenceProducts = referenceProducts;
    }

    /**
     * Add new reference image
     * @param productDTO reference image
     */
    public void addReferenceProduct(ProductDTO productDTO) {
        if (!this.referenceProducts.contains(productDTO))
            this.referenceProducts.add(productDTO);
    }

    /**
     * Add products as reference images
     * @param referenceImages list of reference images
     */
    public void addReferenceProduct(List<ProductDTO> referenceImages) {
        if (referenceImages.size() > 0) {
            referenceImages.forEach(i->{
                if (!this.referenceProducts.contains(i))
                    this.referenceProducts.add(i);
            });
        }
    }

    /**
     * Remove reference image
     * @param productDTO reference image
     */
    public void removeReferenceProduct(ProductDTO productDTO) {
        this.referenceProducts.remove(productDTO);
    }

    /**
     * Add new workflow to product list. Only a workflow per product type is allowed
     * @param workflow Workflow to add
     */
    public void addWorkflow(WorkflowDTO workflow) {
        boolean existsWorkflowForType = workflows.stream()
                .filter(w->w.getType()==workflow.getType())
                .findAny()
                .orElse(null) != null;


        if (!existsWorkflowForType) {
            this.workflows.add(workflow);
        }

    }

    /**
     * Add new workflow
     * @param workflow Workflow
     */
    public void addWorkflow(List<WorkflowDTO> workflow) {
        workflow.forEach(this::addWorkflow);
    }

    /**
     * Get workflow of workflowtype
     * @param type WorkFlowType
     * @return workflow
     */
    public WorkflowDTO getWorkflow(WorkflowType type) {
        return workflows.stream()
                .filter(w->w.getType() == type)
                .findAny()
                .orElse(null);
    }

    /**
     * get workflows
     * @return list of workflows
     */
    public ObservableList<WorkflowDTO> getWorkflows() {
        return workflows;
    }

    /**
     * Set workflows
     * @param workflows worflows
     */
    public void setWorkflows(ObservableList<WorkflowDTO> workflows) {
        this.workflows = workflows;
    }

    /**
     * Remove workflow
     * @param workflowDTO workflow
     */
    public void removeWorkflow(WorkflowDTO workflowDTO) {
        workflows.remove(workflowDTO);
    }
}

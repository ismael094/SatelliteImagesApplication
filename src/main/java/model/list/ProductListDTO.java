package model.list;

import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import model.products.ProductDTO;
import model.restriction.Restriction;
import org.bson.types.ObjectId;
import org.locationtech.jts.io.ParseException;
import services.database.ProductListDBDAO;
import utils.WKTUtil;

import java.util.*;

public class ProductListDTO {
    private List<ProductListDTOChangeListener> listeners;
    private ObjectId id;
    private StringProperty name;
    private StringProperty description;
    private ObservableList<ProductDTO> products;
    private final DoubleProperty sizeAsDouble;
    private final IntegerProperty count;
    private List<Restriction> restrictions;
    private ObservableList<String> areasOfWork;

    public ProductListDTO(StringProperty name, StringProperty description) {
        this.listeners = new ArrayList<>();
        this.name = name;
        this.description = description;
        this.products = FXCollections.observableArrayList();
        this.sizeAsDouble = new SimpleDoubleProperty();
        this.count  = new SimpleIntegerProperty();
        this.restrictions = new ArrayList<>();
        this.areasOfWork = FXCollections.observableArrayList();
    }

    public void addListener(ProductListDTOChangeListener listener) {
        listeners.add(listener);
    }

    private void save() {
        //ProductListEvent event = new ProductListEvent(this,"change");
        //listeners.forEach(l->l.onProductListChange(event));
        ProductListDTO p = this;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                ProductListDBDAO.getInstance().save(p);
                return null;
            }
        };
        new Thread(task).start();

    }


    public ProductListDTO() {
        this.products = FXCollections.observableArrayList();
        this.sizeAsDouble = new SimpleDoubleProperty();
        this.count  = new SimpleIntegerProperty();
        this.restrictions = new ArrayList<>();
        this.areasOfWork = FXCollections.observableArrayList();
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
        save();
        return true;
    }

    public boolean addProduct(List<ProductDTO> products) {
        products.forEach(p->{
            if (!this.products.contains(p) && validate(p))
                this.products.add(p);
        });
        reloadProperties();
        save();
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
        save();
    }

    public void remove(Collection<ProductDTO> product) {
        products.removeAll(product);
        reloadProperties();
        save();
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

    public void setSizeAsDouble(double sizeAsDouble) {
        this.sizeAsDouble.set(sizeAsDouble);
    }

    public int getCount() {
        return count.get();
    }

    public IntegerProperty countProperty() {
        return count;
    }

    public void setCount(int count) {
        this.count.set(count);
    }

    public void addRestriction(Restriction restriction) {
        restrictions.add(restriction);
        save();
    }

    private boolean validate(ProductDTO product) {
        if (restrictions.size() == 0)
            return true;
        for (Restriction r : restrictions)
            if (!r.validate(product))
                return false;
        return true;
    }

    public ObservableList<String> getAreasOfWork() {
        return areasOfWork;
    }

    public Map<String,List<String>> getProductsAreasOfWorks() {
        Map<String, List<String>> areas = new HashMap<>();
        products.forEach(p->{
            areas.put(p.getId(), areasOfWorkOfProduct(p.getFootprint()));
        });
        return areas;
    }

    public List<String> areasOfWorkOfProduct(String footprint) {
        if (areasOfWork.size() == 0)
            return null;
        List<String> areas = new ArrayList<>();
        areasOfWork.forEach(a->{
            try {
                if (WKTUtil.workingAreaContains(footprint,a))
                    areas.add(a);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return areas;
    }

    public void addAreaOfWork(String area) {
        this.areasOfWork.add(area);
        save();
    }

    public void removeAreaOfWork(String area) {
        this.areasOfWork.remove(area);
        save();
    }

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

}

package services.database;

import dev.morphia.query.experimental.filters.Filters;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import model.products.Sentinel2ProductDTO;
import services.entities.Product;
import services.entities.Sentinel1Product;
import services.entities.Sentinel2Product;
import utils.database.MongoDBManager;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ProductDBDAO implements DAO<ProductDTO> {

    private static ProductDBDAO instance;
    private final MongoDBManager database;
    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private ProductDBDAO() {
        database = MongoDBManager.getMongoDBManager();
    }

    public static ProductDBDAO getInstance() {
        if (instance == null) {
            instance = new ProductDBDAO();
        }
        return instance;
    }

    @Override
    public List<ProductDTO> getCollection() {
        return toDAO(database.getDatastore().find(Product.class).iterator().toList());
    }

    @Override
    public List<ProductDTO> find(ProductDTO dao) {
        return toDAO(database.getDatastore()
                .find(Product.class)
                .filter(Filters.eq("id", dao.getId()))
                .iterator()
                .toList());
    }

    @Override
    public ProductDTO findFirst(ProductDTO dao) {
        return toDAO(database.getDatastore()
                .find(Product.class)
                .filter(Filters.eq("id", dao.getId()))
                .first());
    }

    @Override
    public void save(ProductDTO dao) {
        database.getDatastore().save(toEntity(dao));
    }

    @Override
    public void delete(ProductDTO dao) {
        database.getDatastore().delete(toEntity(dao));
    }

    public List<ProductDTO> toDAO(List<Product> toList) {
        if (toList == null)
            return null;
        List<ProductDTO> result = new ArrayList<>();
        toList.forEach(e->result.add(toDAO(e)));
        return result;
    }

    public ProductDTO toDAO(Product product) {
        if (product == null)
            return null;
        if (product instanceof Sentinel1Product)
            return sentinel1Product((Sentinel1Product) product);
        else if (product instanceof Sentinel2Product)
            return sentinel2Product((Sentinel2Product)product);
        else
            return productDTO(product);
    }

    private ProductDTO sentinel1Product(Sentinel1Product product) {
        Sentinel1ProductDTO s1 = new Sentinel1ProductDTO(
                new SimpleStringProperty(product.getId()),new SimpleStringProperty(product.getTitle()),
                new SimpleStringProperty(product.getPlatformName()),new SimpleStringProperty(product.getProductType()),
                new SimpleStringProperty(product.getFootprint()),new SimpleStringProperty(product.getSize()),
                new SimpleStringProperty(product.getStatus()),new SimpleObjectProperty<>(DatatypeConverter.parseDateTime(product.getIngestionDate())),
                new SimpleStringProperty(product.getSensorOperationalMode()),new SimpleStringProperty(product.getPolarizationMode()));
        s1.setIngestionDate(product.getIngestionDate());
        return s1;
    }

    private ProductDTO sentinel2Product(Sentinel2Product product) {
        Sentinel2ProductDTO s2 = new Sentinel2ProductDTO(
                new SimpleStringProperty(product.getId()),new SimpleStringProperty(product.getTitle()),
                new SimpleStringProperty(product.getPlatformName()),new SimpleStringProperty(product.getProductType()),
                new SimpleStringProperty(product.getFootprint()),new SimpleStringProperty(product.getSize()),
                new SimpleStringProperty(product.getStatus()),new SimpleObjectProperty<>(DatatypeConverter.parseDateTime(product.getIngestionDate())),
                new SimpleDoubleProperty(product.getCloudCoverPercentage()),new SimpleDoubleProperty(product.getVegetationPercentageCoverage()),
                new SimpleDoubleProperty(product.getCloudCoverPercentage()),new SimpleDoubleProperty(product.getWaterPercentageCoverage()));
        s2.setIngestionDate(product.getIngestionDate());
        return s2;
    }

    private ProductDTO productDTO(Product product) {
        ProductDTO p = new ProductDTO(
                new SimpleStringProperty(product.getId()), new SimpleStringProperty(product.getTitle()),
                new SimpleStringProperty(product.getPlatformName()), new SimpleStringProperty(product.getProductType()),
                new SimpleStringProperty(product.getFootprint()), new SimpleStringProperty(product.getSize()),
                new SimpleStringProperty(product.getStatus()), new SimpleObjectProperty<Calendar>(DatatypeConverter.parseDateTime(product.getIngestionDate())));
        p.setIngestionDate(product.getIngestionDate());
        return p;
    }

    private Product sentinel1Product(Sentinel1ProductDTO productDTO) {
        return new Sentinel1Product(
                productDTO.getId(),productDTO.getTitle(),
                productDTO.getPlatformName(),productDTO.getProductType(),
                productDTO.getFootprint(),productDTO.getSize(),
                productDTO.getStatus(),getIngestionDate(productDTO.getIngestionDate()),
                productDTO.getSensorOperationalMode(),productDTO.getPolarizationMode());
    }

    private Product sentinel2Product(Sentinel2ProductDTO productDTO) {
        return new Sentinel2Product(
                productDTO.getId(),productDTO.getTitle(),
                productDTO.getPlatformName(),productDTO.getProductType(),
                productDTO.getFootprint(),productDTO.getSize(),
                productDTO.getStatus(),getIngestionDate(productDTO.getIngestionDate()),
                productDTO.getCloudCoverPercentage(),productDTO.getVegetationPercentageCoverage(),
                productDTO.getCloudCoverPercentage(),productDTO.getWaterPercentageCoverage());
    }

    private Product productDTO(ProductDTO productDTO) {
        return new Product(
                productDTO.getId(),productDTO.getTitle(),
                productDTO.getPlatformName(),productDTO.getProductType(),
                productDTO.getFootprint(),productDTO.getSize(),
                productDTO.getStatus(),getIngestionDate(productDTO.getIngestionDate()));
    }

    public List<Product> toEntity(List<ProductDTO> productDTO) {
        if (productDTO == null)
            return null;
        List<Product> result = new ArrayList<>();
        productDTO.forEach(e->result.add(toEntity(e)));
        return result;
    }

    public Product toEntity(ProductDTO productDTO) {
        if (productDTO == null)
            return null;
        if (productDTO instanceof Sentinel1ProductDTO)
            return sentinel1Product((Sentinel1ProductDTO) productDTO);
        else if (productDTO instanceof Sentinel2ProductDTO)
            return sentinel2Product((Sentinel2ProductDTO)productDTO);
        else
            return productDTO(productDTO);
    }

    private String getIngestionDate(Calendar ingestionDate) {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(ingestionDate.getTime()).replace(" ","T")+"Z";
    }
}

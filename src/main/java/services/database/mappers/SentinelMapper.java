package services.database.mappers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import model.products.Sentinel2ProductDTO;
import model.products.SentinelProductDTO;
import services.entities.Product;
import services.entities.Sentinel1Product;
import services.entities.Sentinel2Product;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class SentinelMapper implements DAOMapper<ProductDTO, Product> {


    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public List<ProductDTO> toDAO(List<Product> toList) {
        if (toList == null)
            return null;
        List<ProductDTO> result = new ArrayList<>();
        toList.forEach(e->result.add(toDAO(e)));
        return result;
    }

    @Override
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

    @Override
    public List<Product> toEntity(List<ProductDTO> productDTO) {
        if (productDTO == null)
            return null;
        List<Product> result = new ArrayList<>();
        productDTO.forEach(e->result.add(toEntity(e)));
        return result;
    }

    @Override
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
        ProductDTO p = new SentinelProductDTO(
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

    private String getIngestionDate(Calendar ingestionDate) {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(ingestionDate.getTime()).replace(" ","T")+"Z";
    }
}

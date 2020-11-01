package services.database.mappers.productmappers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.sentinel.Sentinel1ProductDTO;
import services.database.mappers.ProductMapper;
import services.entities.Product;
import services.entities.Sentinel1Product;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Sentinel1ProductMapper implements ProductMapper {

    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public ProductDTO getDTOProduct(Product product) {
        Sentinel1Product sentinel1 = (Sentinel1Product)product;
        Sentinel1ProductDTO s1 = new Sentinel1ProductDTO(
                new SimpleStringProperty(sentinel1.getId()),new SimpleStringProperty(sentinel1.getTitle()),
                new SimpleStringProperty(sentinel1.getPlatformName()),new SimpleStringProperty(sentinel1.getProductType()),
                new SimpleStringProperty(sentinel1.getFootprint()),new SimpleStringProperty(sentinel1.getSize()),
                new SimpleStringProperty(sentinel1.getStatus()),new SimpleObjectProperty<>(DatatypeConverter.parseDateTime(sentinel1.getIngestionDate())),
                new SimpleStringProperty(sentinel1.getSensorOperationalMode()),new SimpleStringProperty(sentinel1.getPolarizationMode()));
        s1.setIngestionDate(sentinel1.getIngestionDate());
        return s1;
    }

    @Override
    public Product getEntityProduct(ProductDTO product) {
        Sentinel1ProductDTO sentinel1 = (Sentinel1ProductDTO)product;
        return new Sentinel1Product(
                sentinel1.getId(),sentinel1.getTitle(),
                sentinel1.getPlatformName(),sentinel1.getProductType(),
                sentinel1.getFootprint(),sentinel1.getSize(),
                sentinel1.getStatus(),getIngestionDate(sentinel1.getIngestionDate()),
                sentinel1.getSensorOperationalMode(),sentinel1.getPolarizationMode());
    }

    private String getIngestionDate(Calendar ingestionDate) {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(ingestionDate.getTime()).replace(" ","T")+"Z";
    }
}

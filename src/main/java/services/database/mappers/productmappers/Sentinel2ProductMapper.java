package services.database.mappers.productmappers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.Sentinel2ProductDTO;
import services.database.mappers.ProductMapper;
import services.entities.Product;
import services.entities.Sentinel2Product;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Sentinel2ProductMapper implements ProductMapper {

    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public ProductDTO getDTOProduct(Product product) {
        Sentinel2Product sentinel2 = (Sentinel2Product)product;
        Sentinel2ProductDTO s2 = new Sentinel2ProductDTO(
                new SimpleStringProperty(sentinel2.getId()),new SimpleStringProperty(sentinel2.getTitle()),
                new SimpleStringProperty(sentinel2.getPlatformName()),new SimpleStringProperty(sentinel2.getProductType()),
                new SimpleStringProperty(sentinel2.getFootprint()),new SimpleStringProperty(sentinel2.getSize()),
                new SimpleStringProperty(sentinel2.getStatus()),new SimpleObjectProperty<>(DatatypeConverter.parseDateTime(sentinel2.getIngestionDate())),
                new SimpleDoubleProperty(sentinel2.getCloudCoverPercentage()),new SimpleDoubleProperty(sentinel2.getVegetationPercentageCoverage()),
                new SimpleDoubleProperty(sentinel2.getCloudCoverPercentage()),new SimpleDoubleProperty(sentinel2.getWaterPercentageCoverage()));
        s2.setIngestionDate(sentinel2.getIngestionDate());
        return s2;
    }

    @Override
    public Product getEntityProduct(ProductDTO product) {
        Sentinel2ProductDTO sentinel2 = (Sentinel2ProductDTO)product;
        return new Sentinel2Product(
                sentinel2.getId(),sentinel2.getTitle(),
                sentinel2.getPlatformName(),sentinel2.getProductType(),
                sentinel2.getFootprint(),sentinel2.getSize(),
                sentinel2.getStatus(),getIngestionDate(sentinel2.getIngestionDate()),
                sentinel2.getCloudCoverPercentage(),sentinel2.getVegetationPercentageCoverage(),
                sentinel2.getCloudCoverPercentage(),sentinel2.getWaterPercentageCoverage());
    }

    private String getIngestionDate(Calendar ingestionDate) {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(ingestionDate.getTime()).replace(" ","T")+"Z";
    }
}

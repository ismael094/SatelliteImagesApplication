package services.database.mappers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.SentinelProductDTO;
import services.entities.Product;
import utils.SatelliteHelper;

import javax.xml.bind.DatatypeConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ProductDAOMapper implements DAOMapper<ProductDTO, Product> {

    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static ProductMapper getMapper(ProductDTO productDTO) {
        return getMapper(productDTO.getPlatformName());
    }

    public static ProductMapper getMapper(Product product) {
        return getMapper(product.getPlatformName());
    }

    private static ProductMapper getMapper(String name) {
        try {
            return (ProductMapper) Class.forName("services.database.mappers.productmappers."+ SatelliteHelper.getSatellite(name.toUpperCase().replaceAll("[ -]", "_")).getName()+"ProductMapper").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            return null;
        }
    }

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

        ProductMapper mapper = getMapper(product);
        if (mapper != null)
            return mapper.getDTOProduct(product);

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

        ProductMapper mapper = getMapper(productDTO);
        if (mapper != null)
            return mapper.getEntityProduct(productDTO);

        return productDTO(productDTO);
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

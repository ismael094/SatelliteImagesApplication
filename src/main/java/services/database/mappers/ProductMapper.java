package services.database.mappers;

import model.products.ProductDTO;
import services.entities.Product;

public interface ProductMapper {
    ProductDTO getDTOProduct(Product product);
    Product getEntityProduct(ProductDTO product);
}

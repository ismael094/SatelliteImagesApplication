package services.database.mappers;

import model.products.ProductDTO;
import services.entities.Product;

import java.util.List;

public interface DAOMapper<T,F> {
    List<T> toDTO(List<F> toList);
    T toDTO(F toList);
    List<F> toEntity(List<T> productDTO);
    F toEntity(T productDTO);
}

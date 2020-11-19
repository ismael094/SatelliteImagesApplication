package services.database.mappers;

import model.products.ProductDTO;
import services.entities.Product;

import java.util.List;

public interface DAOMapper<T,F> {
    /**
     * Entities to DTOs
     * @param toList entities
     * @return DTOs
     */
    List<T> toDTO(List<F> toList);
    /**
     * From entity to DTO
     * @param toList entity
     * @return DTO
     */
    T toDTO(F toList);
    /**
     * From DTOs to entities
     * @param productDTO DTOs
     * @return entities
     */
    List<F> toEntity(List<T> productDTO);
    /**
     * From DTO to entity
     * @param productDTO DTO
     * @return entity
     */
    F toEntity(T productDTO);
}

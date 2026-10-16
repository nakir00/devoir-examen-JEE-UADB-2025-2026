package sn.uadb.devoir.mapper;

import org.springframework.stereotype.Component;

import sn.uadb.devoir.dto.ProductRequestDto;
import sn.uadb.devoir.dto.ProductResponseDto;
import sn.uadb.devoir.entity.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDto request) {
        return new Product(
                null,
                request.getName(),
                request.getCategory(),
                request.getPrice(),
                request.getQuantity());
    }

    public ProductResponseDto toResponseDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getQuantity());
    }

    public void updateEntity(Product product, ProductRequestDto request) {
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
    }
}

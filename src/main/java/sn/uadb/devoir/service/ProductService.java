package sn.uadb.devoir.service;

import java.math.BigDecimal;
import java.util.List;

import sn.uadb.devoir.dto.ProductRequestDto;
import sn.uadb.devoir.dto.ProductResponseDto;

public interface ProductService {

    ProductResponseDto create(ProductRequestDto request);

    List<ProductResponseDto> findAll();

    ProductResponseDto findById(Long id);

    ProductResponseDto update(Long id, ProductRequestDto request);

    void delete(Long id);

    List<ProductResponseDto> findByCategory(String category);

    List<ProductResponseDto> searchByName(String name);

    List<ProductResponseDto> findLowStock(Integer threshold);

    List<ProductResponseDto> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}

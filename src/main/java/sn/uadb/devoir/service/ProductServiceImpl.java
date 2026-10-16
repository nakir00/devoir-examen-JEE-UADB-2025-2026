package sn.uadb.devoir.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sn.uadb.devoir.dto.ProductRequestDto;
import sn.uadb.devoir.dto.ProductResponseDto;
import sn.uadb.devoir.entity.Product;
import sn.uadb.devoir.exception.ResourceNotFoundException;
import sn.uadb.devoir.mapper.ProductMapper;
import sn.uadb.devoir.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponseDto create(ProductRequestDto request) {
        Product product = productMapper.toEntity(request);
        return productMapper.toResponseDto(productRepository.save(product));
    }

    @Override
    public List<ProductResponseDto> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    @Override
    public ProductResponseDto findById(Long id) {
        return productMapper.toResponseDto(findProductById(id));
    }

    @Override
    @Transactional
    public ProductResponseDto update(Long id, ProductRequestDto request) {
        Product product = findProductById(id);
        productMapper.updateEntity(product, request);
        return productMapper.toResponseDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        productRepository.delete(findProductById(id));
    }

    @Override
    public List<ProductResponseDto> findByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> findLowStock(Integer threshold) {
        return productRepository.findByQuantityLessThan(threshold).stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Le prix minimum doit être inférieur ou égal au prix maximum.");
        }

        return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(productMapper::toResponseDto)
                .toList();
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable avec l'id : " + id));
    }
}

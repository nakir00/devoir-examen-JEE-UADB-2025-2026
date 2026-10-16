package sn.uadb.devoir.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.uadb.devoir.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByQuantityLessThan(Integer quantity);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}

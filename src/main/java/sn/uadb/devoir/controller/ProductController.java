package sn.uadb.devoir.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import sn.uadb.devoir.dto.ProductRequestDto;
import sn.uadb.devoir.dto.ProductResponseDto;
import sn.uadb.devoir.service.ProductService;

@RestController
@Validated
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findById(
            @PathVariable @Positive(message = "L'identifiant doit être strictement positif.") Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable @Positive(message = "L'identifiant doit être strictement positif.") Long id,
            @Valid @RequestBody ProductRequestDto request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "L'identifiant doit être strictement positif.") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDto>> findByCategory(
            @PathVariable @NotBlank(message = "La catégorie est obligatoire.") String category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchByName(
            @RequestParam @NotBlank(message = "Le nom recherché est obligatoire.") String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDto>> findLowStock(
            @RequestParam @PositiveOrZero(message = "Le seuil doit être positif ou nul.") Integer threshold) {
        return ResponseEntity.ok(productService.findLowStock(threshold));
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<ProductResponseDto>> findByPriceRange(
            @RequestParam @DecimalMin(value = "0.0", message = "Le prix minimum doit être positif ou nul.") BigDecimal min,
            @RequestParam @DecimalMin(value = "0.0", message = "Le prix maximum doit être positif ou nul.") BigDecimal max) {
        return ResponseEntity.ok(productService.findByPriceRange(min, max));
    }
}

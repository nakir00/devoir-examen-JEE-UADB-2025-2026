package sn.uadb.devoir.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProductRequestDto {

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
    private String name;

    @NotBlank(message = "La catégorie est obligatoire.")
    @Size(max = 100, message = "La catégorie ne doit pas dépasser 100 caractères.")
    private String category;

    @NotNull(message = "Le prix est obligatoire.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif ou nul.")
    @Digits(integer = 10, fraction = 2, message = "Le prix doit avoir au plus 10 chiffres entiers et 2 décimales.")
    private BigDecimal price;

    @NotNull(message = "La quantité est obligatoire.")
    @PositiveOrZero(message = "La quantité doit être positive ou nulle.")
    private Integer quantity;

    public ProductRequestDto() {
    }

    public ProductRequestDto(String name, String category, BigDecimal price, Integer quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

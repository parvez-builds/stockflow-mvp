package com.stockflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProductForm {
    @NotBlank
    private String name;

    @NotBlank
    private String sku;

    private String description;

    @NotNull @Min(0)
    private Integer quantityOnHand = 0;

    @DecimalMin("0")
    private BigDecimal costPrice;

    @DecimalMin("0")
    private BigDecimal sellingPrice;

    @Min(0)
    private Integer lowStockThreshold;
}
package com.stockflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "products",
       uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "sku"}))
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Organization organization;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sku;

    private String description;

    @Column(nullable = false)
    private Integer quantityOnHand = 0;

    private BigDecimal costPrice;
    private BigDecimal sellingPrice;

    private Integer lowStockThreshold;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;

    private String lastUpdatedBy;

    public boolean isLowStock(int defaultThreshold) {
        int limit = (lowStockThreshold != null) ? lowStockThreshold : defaultThreshold;
        return quantityOnHand <= limit;
    }
}
package com.stockflow.service;

import com.stockflow.entity.Product;
import com.stockflow.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ProductRepository products;
    private final SettingsService settings;

    public record DashboardData(long totalProducts, long totalQuantity,
                                List<Product> lowStock, int defaultThreshold) {}

    @Transactional(readOnly = true)
    public DashboardData build(Long orgId) {
        int defaultThreshold = settings.getThreshold(orgId);
        List<Product> all = products.findByOrganizationIdOrderByNameAsc(orgId);

        long totalQuantity = all.stream()
                .mapToLong(Product::getQuantityOnHand)
                .sum();

        List<Product> lowStock = all.stream()
                .filter(p -> p.isLowStock(defaultThreshold))
                .toList();

        return new DashboardData(all.size(), totalQuantity, lowStock, defaultThreshold);
    }
}
package com.stockflow.service;

import com.stockflow.dto.ProductForm;
import com.stockflow.entity.Product;
import com.stockflow.repository.OrganizationRepository;
import com.stockflow.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository products;
    private final OrganizationRepository orgs;

    @Transactional(readOnly = true)
    public List<Product> list(Long orgId, String q) {
        if (q == null || q.isBlank()) {
            return products.findByOrganizationIdOrderByNameAsc(orgId);
        }
        return products.search(orgId, q.trim());
    }

    @Transactional(readOnly = true)
    public Product get(Long orgId, Long id) {
        return products.findByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void create(Long orgId, String email, ProductForm f) {
        if (products.existsByOrganizationIdAndSku(orgId, f.getSku().trim())) {
            throw new IllegalArgumentException("SKU already exists");
        }
        Product p = new Product();
        p.setOrganization(orgs.getReferenceById(orgId));
        apply(p, f, email);
        products.save(p);
    }

    @Transactional
    public void update(Long orgId, Long id, String email, ProductForm f) {
        Product p = get(orgId, id);
        products.findByOrganizationIdAndSku(orgId, f.getSku().trim())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new IllegalArgumentException("SKU already exists"); });
        apply(p, f, email);
    }

    @Transactional
    public void delete(Long orgId, Long id) {
        products.delete(get(orgId, id));
    }

    @Transactional
    public void adjust(Long orgId, Long id, int delta, String email) {
        Product p = get(orgId, id);
        int qty = p.getQuantityOnHand() + delta;
        if (qty < 0) {
            throw new IllegalArgumentException("Stock cannot go below zero");
        }
        p.setQuantityOnHand(qty);
        p.setLastUpdatedBy(email);
    }

    private void apply(Product p, ProductForm f, String email) {
        p.setName(f.getName().trim());
        p.setSku(f.getSku().trim());
        p.setDescription(f.getDescription());
        p.setQuantityOnHand(f.getQuantityOnHand());
        p.setCostPrice(f.getCostPrice());
        p.setSellingPrice(f.getSellingPrice());
        p.setLowStockThreshold(f.getLowStockThreshold());
        p.setLastUpdatedBy(email);
    }
}
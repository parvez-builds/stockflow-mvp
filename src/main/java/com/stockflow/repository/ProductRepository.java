package com.stockflow.repository;

import com.stockflow.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOrganizationIdOrderByNameAsc(Long orgId);
    Optional<Product> findByIdAndOrganizationId(Long id, Long orgId);
    Optional<Product> findByOrganizationIdAndSku(Long orgId, String sku);
    boolean existsByOrganizationIdAndSku(Long orgId, String sku);

    @Query("select p from Product p where p.organization.id = :orgId and " +
           "(lower(p.name) like lower(concat('%', :q, '%')) or " +
           "lower(p.sku) like lower(concat('%', :q, '%'))) " +
           "order by p.name")
    List<Product> search(@Param("orgId") Long orgId, @Param("q") String q);
}
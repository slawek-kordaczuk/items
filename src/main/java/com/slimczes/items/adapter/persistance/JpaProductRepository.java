package com.slimczes.items.adapter.persistance;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.slimczes.items.adapter.persistance.entity.ProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {

    @EntityGraph(attributePaths = {"productReservations"})
    Optional<ProductEntity> findBySku(String sku);
    List<ProductEntity> findAllBySkuIn(Collection<String> skus);

}

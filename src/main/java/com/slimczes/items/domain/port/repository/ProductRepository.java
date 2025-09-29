package com.slimczes.items.domain.port.repository;

import java.util.Optional;
import java.util.UUID;

import com.slimczes.items.domain.model.Product;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findBySku(String sku);
}

package com.slimczes.items.domain.port.repository;

import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.model.ProductReservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findBySku(String sku);
}

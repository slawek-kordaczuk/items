package com.slimczes.items.adapter.persistance;

import java.util.Optional;
import java.util.UUID;

import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.port.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ProductRepositoryService implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductEntityMapper productEntityMapper;

    @Override
    public Product save(Product product) {
        ProductEntity productEntity = jpaProductRepository.save(productEntityMapper.toEntity(product));
        return productEntityMapper.toProduct(productEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaProductRepository.findById(id)
                                   .map(productEntityMapper::toProduct);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpaProductRepository.findBySku(sku)
                                   .map(productEntityMapper::toProduct);
    }
}

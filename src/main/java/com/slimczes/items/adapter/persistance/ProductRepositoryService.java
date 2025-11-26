package com.slimczes.items.adapter.persistance;

import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.port.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class ProductRepositoryService implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductEntityMapper productEntityMapper;

    @Override
    @Transactional
    public Product save(Product product) {
        ProductEntity productEntity = jpaProductRepository.save(productEntityMapper.toEntity(product));
        return productEntityMapper.toProduct(productEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findBySku(String sku) {
        return jpaProductRepository.findBySku(sku)
                .map(productEntityMapper::toProduct);
    }
}

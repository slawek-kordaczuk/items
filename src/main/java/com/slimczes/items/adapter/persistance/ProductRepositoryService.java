package com.slimczes.items.adapter.persistance;

import com.slimczes.items.adapter.persistance.entity.ProductEntity;
import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.port.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    public Optional<Product> findBySku(String sku) {
        return jpaProductRepository.findBySku(sku)
                .map(productEntityMapper::toProduct);
    }

    @Override
    public List<Product> findAllBySkuIn(Collection<String> skus) {
        return jpaProductRepository.findAllBySkuIn(skus).stream()
                .map(productEntityMapper::toProduct)
                .toList();
    }
}

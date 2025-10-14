package com.slimczes.items.service.product;

import com.slimczes.items.api.CreateProductResponse;
import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.port.repository.ProductRepository;
import com.slimczes.items.service.product.dto.CreateProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public CreateProductResponse createProduct(CreateProductDto createProductDto){
        Product product = new Product(createProductDto.sku(), createProductDto.name(), createProductDto.quantity());
        Product savedProduct = productRepository.save(product);
        return productMapper.responseFromProduct(savedProduct);
    }



}

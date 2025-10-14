package com.slimczes.items.service.product;

import com.slimczes.items.TestcontainersConfiguration;
import com.slimczes.items.api.CreateProductResponse;
import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.port.repository.ProductRepository;
import com.slimczes.items.service.product.dto.CreateProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldCreateProductSuccessfully() {
        // Given
        CreateProductDto createProductDto = new CreateProductDto(
                "NEW-001",
                "New Test Product",
                75
        );

        // When
        CreateProductResponse response = productService.createProduct(createProductDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.sku()).isEqualTo("NEW-001");
        assertThat(response.name()).isEqualTo("New Test Product");

        Optional<Product> savedProduct = productRepository.findBySku("NEW-001");
        assertThat(savedProduct).isPresent();
        assertThat(savedProduct.get().getSku()).isEqualTo("NEW-001");
        assertThat(savedProduct.get().getName()).isEqualTo("New Test Product");
        assertThat(savedProduct.get().getAvailableQuantity()).isEqualTo(75);
    }

}

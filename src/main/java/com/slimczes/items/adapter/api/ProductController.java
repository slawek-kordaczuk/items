package com.slimczes.items.adapter.api;

import com.slimczes.items.service.product.ProductService;
import com.slimczes.items.service.product.dto.CreateProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<CreateProductResponse> createProduct(CreateProductDto createProductDto) {
        CreateProductResponse response = productService.createProduct(createProductDto);
        return ResponseEntity.ok(response);
    }

}

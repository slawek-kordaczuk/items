package com.slimczes.items.api;

import com.slimczes.items.service.product.ProductService;
import com.slimczes.items.service.product.dto.CreateProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<CreateProductResponse> createProduct(@RequestBody CreateProductDto createProductDto) {
        CreateProductResponse response = productService.createProduct(createProductDto);
        return ResponseEntity.ok(response);
    }

}

package com.slimczes.items.service.product.dto;

public record CreateProductDto (
    String sku,
    String name,
    int quantity
) {

}

package com.slimczes.items.api;

import java.util.UUID;

public record CreateProductResponse(
    UUID id,
    String sku,
    String name
) {

}

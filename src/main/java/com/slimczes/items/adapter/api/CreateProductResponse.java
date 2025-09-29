package com.slimczes.items.adapter.api;

import java.util.UUID;

public record CreateProductResponse(
    UUID id,
    String sku,
    String name
) {

}

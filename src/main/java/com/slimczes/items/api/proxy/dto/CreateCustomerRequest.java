package com.slimczes.items.api.proxy.dto;

public record CreateCustomerRequest(
        String email,
        String name
) {
}

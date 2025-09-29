package com.slimczes.items.adapter.messaging.event;

public record ReservationItem(
    String productSku,
    String name,
    int quantity
) {
}

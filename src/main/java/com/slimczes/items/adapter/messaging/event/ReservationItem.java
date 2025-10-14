package com.slimczes.items.adapter.messaging.event;

public record ReservationItem(
    String sku,
    String name,
    int quantity
) {
}

package com.slimczes.items.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ItemsReserved(
    UUID eventId,
    UUID orderId,
    List<ReservedItem> reservedItems,
    Instant occurredAt
) {

    public ItemsReserved(UUID orderId, List<ReservedItem> reservedItems, Instant occurredAt) {
        this(UUID.randomUUID(), orderId, reservedItems, occurredAt);
    }

    public record ReservedItem(
        UUID itemId,
        String sku,
        String name,
        int reservedQuantity
    ) {

    }
}

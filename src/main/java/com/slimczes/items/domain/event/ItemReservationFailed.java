package com.slimczes.items.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.slimczes.items.domain.model.ReservationStatus;

public record ItemReservationFailed(
    UUID eventId,
    UUID orderId,
    List<FailedItem> failedItems,
    Instant occurredAt
) {

    public ItemReservationFailed(UUID orderId, List<FailedItem> failedItems, Instant occurredAt) {
        this(UUID.randomUUID(), orderId, failedItems, occurredAt);
    }

    public record FailedItem(
        String sku,
        ReservationStatus reason,
        int requestedQuantity,
        int availableQuantity
    ) {}
}

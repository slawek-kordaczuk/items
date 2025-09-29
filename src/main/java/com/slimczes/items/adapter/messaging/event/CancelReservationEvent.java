package com.slimczes.items.adapter.messaging.event;

import java.util.List;
import java.util.UUID;

public record CancelReservationEvent(
    UUID eventId,
    UUID orderId,
    UUID customerId,
    List<ReservationItem> items,
    String reason
) {
}

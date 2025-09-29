package com.slimczes.items.adapter.messaging.event;

import java.util.List;
import java.util.UUID;

public record CreateReservationEvent(
    UUID eventId,
    UUID orderId,
    UUID customerId,
    List<ReservationItem> items
){

}

package com.slimczes.items.service.reservation.dto;

import java.util.List;
import java.util.UUID;

public record CancelReservationDto(
    UUID orderId,
    UUID customerId,
    String reason,
    List<ReservationItemDto> items
) {
}

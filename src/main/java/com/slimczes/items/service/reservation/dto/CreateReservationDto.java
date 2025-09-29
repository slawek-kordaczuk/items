package com.slimczes.items.service.reservation.dto;

import java.util.List;
import java.util.UUID;

public record CreateReservationDto(
    UUID orderId,
    UUID customerId,
    List<ReservationItemDto> items
){

}

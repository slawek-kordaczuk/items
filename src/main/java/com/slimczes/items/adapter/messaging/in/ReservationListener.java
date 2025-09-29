package com.slimczes.items.adapter.messaging.in;

import com.slimczes.items.adapter.messaging.event.CancelReservationEvent;
import com.slimczes.items.adapter.messaging.event.CreateReservationEvent;
import com.slimczes.items.adapter.messaging.mapper.CreateReservationMapper;
import com.slimczes.items.service.reservation.ReservationService;
import com.slimczes.items.service.reservation.dto.CancelReservationDto;
import com.slimczes.items.service.reservation.dto.CreateReservationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationListener {

    private final ReservationService reservationService;
    private final CreateReservationMapper createReservationMapper;

    @KafkaListener(
        topics = "${kafka.topics.reservation}",
        groupId = "${kafka.consumer.group-id}"
    )
    public void handleItemsToReserve(CreateReservationEvent event) {
        CreateReservationDto createReservationDto = createReservationMapper.toOrderReservationFromEvent(event);
        reservationService.createReservation(createReservationDto);
    }

    @KafkaListener(
        topics = "${kafka.topics.reservation-cancelled}",
        groupId = "${kafka.consumer.group-id}"
    )
    public void handleItemsToCancelReservation(CancelReservationEvent event) {
        CancelReservationDto cancelReservationDto = createReservationMapper.toCancelOrderReservationFromEvent(event);
        reservationService.cancelReservation(cancelReservationDto);
    }

}

package com.slimczes.items.adapter.messaging.in;

import com.slimczes.items.adapter.messaging.event.CancelReservationEvent;
import com.slimczes.items.adapter.messaging.event.CreateReservationEvent;
import com.slimczes.items.adapter.messaging.mapper.CreateReservationMapper;
import com.slimczes.items.service.reservation.CancelReservation;
import com.slimczes.items.service.reservation.CreateReservation;
import com.slimczes.items.service.reservation.dto.CancelReservationDto;
import com.slimczes.items.service.reservation.dto.CreateReservationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationListener {

    private final CreateReservation createReservation;
    private final CancelReservation cancelReservation;
    private final CreateReservationMapper createReservationMapper;

    @KafkaListener(
            topics = "${kafka.topics.reservation}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "createReservationKafkaListenerContainerFactory"
    )
    public void handleItemsToReserve(CreateReservationEvent event) {
        log.info("Received reservation event: {}", event);
        CreateReservationDto createReservationDto = createReservationMapper.toOrderReservationFromEvent(event);
        createReservation.createReservation(createReservationDto);
    }

    @KafkaListener(
            topics = "${kafka.topics.reservation-cancelled}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "cancelReservationKafkaListenerContainerFactory"
    )
    public void handleItemsToCancelReservation(CancelReservationEvent event) {
        log.info("Received cancel reservation event: {}", event);
        CancelReservationDto cancelReservationDto = createReservationMapper.toCancelOrderReservationFromEvent(event);
        cancelReservation.cancelReservation(cancelReservationDto);
    }

}

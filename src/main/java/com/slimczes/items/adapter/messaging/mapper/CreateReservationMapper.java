package com.slimczes.items.adapter.messaging.mapper;

import com.slimczes.items.adapter.messaging.event.CancelReservationEvent;
import com.slimczes.items.adapter.messaging.event.CreateReservationEvent;
import com.slimczes.items.service.reservation.dto.CancelReservationDto;
import com.slimczes.items.service.reservation.dto.CreateReservationDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreateReservationMapper {

    CreateReservationDto toOrderReservationFromEvent(CreateReservationEvent itemsToReserveEvent);
    CancelReservationDto toCancelOrderReservationFromEvent(CancelReservationEvent cancelReservationEvent);

}

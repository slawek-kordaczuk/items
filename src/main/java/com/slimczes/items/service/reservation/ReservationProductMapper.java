package com.slimczes.items.service.reservation;

import com.slimczes.items.domain.event.ItemReservationFailed;
import com.slimczes.items.domain.event.ItemsReserved;
import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.model.ProductReservationStatus;
import com.slimczes.items.service.reservation.dto.ReservationItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ReservationProductMapper {

    @Mapping(target = "itemId", source = "product.id")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "reservedQuantity", source = "reservationItemDto.quantity")
    ItemsReserved.ReservedItem toReservedItem(Product product, ReservationItemDto reservationItemDto);

    @Mapping(target = "sku", source = "reservationItemDto.sku")
    @Mapping(target = "reason", source = "reservationStatus")
    @Mapping(target = "requestedQuantity", source = "reservationItemDto.quantity")
    @Mapping(target = "availableQuantity", constant = "0")
    ItemReservationFailed.FailedItem toFailedItem(ReservationItemDto reservationItemDto, ProductReservationStatus reservationStatus);

    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "reason", source = "reservationStatus")
    @Mapping(target = "requestedQuantity", source = "reservationItemDto.quantity")
    @Mapping(target = "availableQuantity", source = "product.availableQuantity")
    ItemReservationFailed.FailedItem toFailedItem(Product product, ReservationItemDto reservationItemDto, ProductReservationStatus reservationStatus);

}

package com.slimczes.items.service.reservation;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.slimczes.items.domain.event.ItemReservationFailed;
import com.slimczes.items.domain.event.ItemsReserved;
import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.model.ReservationResult;
import com.slimczes.items.domain.model.ReservationStatus;
import com.slimczes.items.domain.port.messaging.ItemReservedPublisher;
import com.slimczes.items.domain.port.repository.ProductRepository;
import com.slimczes.items.service.reservation.dto.CancelReservationDto;
import com.slimczes.items.service.reservation.dto.CreateReservationDto;
import com.slimczes.items.service.reservation.dto.ReservationItemDto;
import com.slimczes.items.service.reservation.mapper.ReservationProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ItemReservedPublisher itemReservedPublisher;
    private final ProductRepository productRepository;
    private final ReservationProductMapper reservationProductMapper;

    public void createReservation(CreateReservationDto createReservationDto) {
        List<ItemsReserved.ReservedItem> successReservations = new ArrayList<>();
        List<ItemReservationFailed.FailedItem> failedReservation = new ArrayList<>();
        createReservationDto.items().forEach(
            item -> productRepository.findBySku(item.sku()).ifPresentOrElse(product ->
                    reserveProduct(item, product, successReservations, failedReservation),
                () -> failedReservation.add(reservationProductMapper.toFailedItem(item, ReservationStatus.NOT_FOUND))));
        publishSucceedReservations(createReservationDto.orderId(), successReservations);
        publishFailedReservations(createReservationDto.orderId(), failedReservation);
    }

    public void cancelReservation(CancelReservationDto cancelReservationDto) {
        cancelReservationDto.items().forEach(item ->
            productRepository.findBySku(item.sku()).ifPresent(product -> {
                product.cancelReserveForOrder(item.quantity());
                productRepository.save(product);
            }));
    }

    private void reserveProduct(ReservationItemDto item, Product product, List<ItemsReserved.ReservedItem> successReservations,
                                List<ItemReservationFailed.FailedItem> failedReservation) {
        ReservationResult reservationResult = product.reserveForOrder(item.quantity());
        if (reservationResult.success()) {
            successReservations.add(reservationProductMapper.toReservedItem(product, item));
            productRepository.save(product);
        } else {
            failedReservation.add(reservationProductMapper.toFailedItem(product, item, reservationResult.reservationStatus()));
        }
    }

    private void publishSucceedReservations(UUID orderId, List<ItemsReserved.ReservedItem> successReservations) {
        if (!successReservations.isEmpty()) {
            ItemsReserved itemsReserved = new ItemsReserved(orderId, successReservations, LocalDateTime.now().toInstant(ZoneOffset.UTC));
            itemReservedPublisher.publishReservedItems(itemsReserved);

        }
    }

    private void publishFailedReservations(UUID orderId, List<ItemReservationFailed.FailedItem> failedReservation) {
        if (!failedReservation.isEmpty()) {
            ItemReservationFailed itemReservationFailed = new ItemReservationFailed(orderId, failedReservation, LocalDateTime.now().toInstant(ZoneOffset.UTC));
            itemReservedPublisher.publishReservedItemsFailed(itemReservationFailed);
        }
    }

}

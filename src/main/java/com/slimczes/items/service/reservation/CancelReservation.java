package com.slimczes.items.service.reservation;

import com.slimczes.items.domain.port.repository.ProductRepository;
import com.slimczes.items.service.reservation.dto.CancelReservationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelReservation {

    private final ProductRepository productRepository;

    @Transactional
    public void cancelReservation(CancelReservationDto cancelReservationDto) {
        log.info("Cancel Reservation for orderId: {}", cancelReservationDto.orderId());
        cancelReservationDto.items().forEach(item ->
                productRepository.findBySku(item.sku()).ifPresentOrElse(product -> {
                    product.cancelReserveForOrder(item.quantity());
                    productRepository.save(product);
                }, () -> log.error("Product not fount for sku: {}", item.sku())));
    }
}

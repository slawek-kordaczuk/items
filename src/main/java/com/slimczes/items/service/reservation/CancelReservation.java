package com.slimczes.items.service.reservation;

import com.slimczes.items.domain.model.Product;
import com.slimczes.items.domain.port.repository.ProductRepository;
import com.slimczes.items.service.reservation.dto.CancelReservationDto;
import com.slimczes.items.service.reservation.dto.ReservationItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelReservation {

    private final ProductRepository productRepository;

    @Transactional
    public void cancelReservation(CancelReservationDto cancelReservationDto) {
        log.info("Cancel Reservation for orderId: {}", cancelReservationDto.orderId());
        List<String> skus = cancelReservationDto.items().stream()
                .map(ReservationItemDto::sku)
                .toList();
        List<Product> products = productRepository.findAllBySkuIn(skus);
        products.forEach(product -> cancelReservationDto.items().stream()
                .filter(item -> item.sku().equals(product.getSku()))
                .findFirst()
                .ifPresentOrElse(item -> {
                    product.cancelReserveForOrder(item.quantity(), cancelReservationDto.orderId());
                    productRepository.save(product);
                }, () -> log.error("No reservation item found for product sku: {}", product.getSku())));
    }
}

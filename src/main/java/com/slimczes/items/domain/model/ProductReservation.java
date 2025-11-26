package com.slimczes.items.domain.model;


import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ProductReservation {

    private final UUID id;
    private final UUID orderId;
    private final int reservedQuantity;
    private final Product product;
    private ProductReservationStatus productReservationStatus;
    private final Instant createdAt;
    private Instant updatedAt;

    @Default
    public ProductReservation(Product product, UUID id, UUID orderId, int reservedQuantity, ProductReservationStatus productReservationStatus,
                              Instant createdAt, Instant updatedAt) {
        this.product = product;
        this.id = id;
        this.orderId = orderId;
        this.reservedQuantity = reservedQuantity;
        this.productReservationStatus = productReservationStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ProductReservation(UUID orderId, int reservedQuantity, Product product) {
        this.id = null;
        this.orderId = orderId;
        this.reservedQuantity = reservedQuantity;
        this.product = product;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public ReservationResult reserveProduct() {
        ReservationResult reservationResult = product.reserveForOrder(reservedQuantity);
        if (reservationResult.success()) {
            this.productReservationStatus = ProductReservationStatus.RESERVED;
        } else {
            this.productReservationStatus = reservationResult.productReservationStatus();
        }
        product.addReservation(this);
        return reservationResult;
    }

    public void cancelReservation() {
        this.productReservationStatus = ProductReservationStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

}

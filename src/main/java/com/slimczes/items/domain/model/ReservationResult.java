package com.slimczes.items.domain.model;

public record ReservationResult(boolean success, ProductReservationStatus productReservationStatus, int reservedQuantity) {
    public static ReservationResult success(int quantity) {
        return new ReservationResult(true, ProductReservationStatus.RESERVED, quantity);
    }

    public static ReservationResult failure(ProductReservationStatus productReservationStatus) {
        return new ReservationResult(false, productReservationStatus, 0);
    }
}

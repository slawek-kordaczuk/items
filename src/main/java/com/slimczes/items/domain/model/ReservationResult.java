package com.slimczes.items.domain.model;

public record ReservationResult(boolean success, ReservationStatus reservationStatus, int reservedQuantity) {
    public static ReservationResult success(int quantity) {
        return new ReservationResult(true, ReservationStatus.SUCCESS, quantity);
    }

    public static ReservationResult failure(ReservationStatus reservationStatus) {
        return new ReservationResult(false, reservationStatus, 0);
    }
}
